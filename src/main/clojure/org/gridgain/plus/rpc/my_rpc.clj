(ns org.gridgain.plus.rpc.my-rpc
    (:require
        [org.gridgain.plus.ddl.my-create-table :as my-create-table]
        [org.gridgain.plus.ddl.my-alter-table :as my-alter-table]
        [org.gridgain.plus.ddl.my-create-index :as my-create-index]
        [org.gridgain.plus.ddl.my-drop-index :as my-drop-index]
        [org.gridgain.plus.ddl.my-drop-table :as my-drop-table]
        [org.gridgain.plus.ddl.my-create-dataset :as my-create-dataset]
        [org.gridgain.plus.ddl.my-drop-dataset :as my-drop-dataset]
        [org.gridgain.plus.dml.my-smart-clj :as my-smart-clj]
        [org.gridgain.plus.dml.select-lexical :as my-lexical]
        [org.gridgain.plus.dml.my-select-plus :as my-select]
        [org.gridgain.plus.dml.my-select-plus-args :as my-select-plus-args]
        [org.gridgain.plus.dml.my-insert :as my-insert]
        [org.gridgain.plus.dml.my-update :as my-update]
        [org.gridgain.plus.dml.my-delete :as my-delete]
        [org.gridgain.plus.dml.my-smart-db-line :as my-smart-db-line]
        [org.gridgain.plus.dml.my-smart-db :as my-smart-db]
        [org.gridgain.plus.dml.my-smart-sql :as my-smart-sql]
        [org.gridgain.plus.tools.my-user-group :as my-user-group]
        [org.gridgain.plus.sql.my-super-sql :as my-super-sql]
        [org.gridgain.plus.dml.my-smart-token-clj :as my-smart-token-clj]
        [clojure.core.reducers :as r]
        [clojure.string :as str])
    (:import (org.apache.ignite Ignite Ignition IgniteCache)
             (org.apache.ignite.internal IgnitionEx)
             (com.google.common.base Strings)
             (cn.plus.model MyCacheEx MyKeyValue MyLogCache SqlType)
             (org.gridgain.dml.util MyCacheExUtil)
             (cn.plus.model.db MyScenesCache ScenesType MyScenesParams MyScenesParamsPk)
             (org.apache.ignite.configuration CacheConfiguration)
             (org.apache.ignite.cache CacheMode CacheAtomicityMode)
             (org.apache.ignite.cache.query FieldsQueryCursor SqlFieldsQuery)
             (org.apache.ignite.binary BinaryObjectBuilder BinaryObject)
             (java.util ArrayList List Hashtable Date Iterator)
             (java.sql Timestamp)
             (java.math BigDecimal)
             )
    (:gen-class
        :implements [org.gridgain.superservice.IMyRpc]
        ; 生成 class 的类名
        :name org.gridgain.plus.rpc.MyRpc
        ; 是否生成 class 的 main 方法
        :main false
        ))

(defn execute-sql-query-lst [^Ignite ignite group_id [f & r] lst-rs ps]
    (if (some? f)
        (if-not (nil? (first f))
            (cond (and (string? (first f)) (my-lexical/is-eq? (first f) "insert")) (recur ignite group_id r (conj lst-rs (my-smart-db-line/query_sql ignite group_id (my-super-sql/cull-semicolon f))))
                  (and (string? (first f)) (my-lexical/is-eq? (first f) "update")) (recur ignite group_id r (conj lst-rs (my-smart-db-line/query_sql ignite group_id (my-super-sql/cull-semicolon f))))
                  (and (string? (first f)) (my-lexical/is-eq? (first f) "delete")) (recur ignite group_id r (conj lst-rs (my-smart-db-line/query_sql ignite group_id (my-super-sql/cull-semicolon f))))
                  (and (string? (first f)) (my-lexical/is-eq? (first f) "select")) (recur ignite group_id r (conj lst-rs (my-smart-db-line/rpc_select_sql ignite group_id (my-super-sql/cull-semicolon f) ps)))
                  ; create dataset
                  (and (string? (first f)) (my-lexical/is-eq? (first f) "create") (my-lexical/is-eq? (second f) "schema")) (let [rs (my-create-dataset/create_data_set ignite group_id (str/join " " (my-super-sql/cull-semicolon f)))]
                                                                                                                                 (if (nil? rs)
                                                                                                                                     (recur ignite group_id r (conj lst-rs "true"))
                                                                                                                                     (recur ignite group_id r (conj lst-rs "false"))
                                                                                                                                     ))
                  ; drop dataset
                  (and (string? (first f)) (my-lexical/is-eq? (first f) "DROP") (my-lexical/is-eq? (second f) "schema")) (let [rs (my-drop-dataset/drop-data-set-lst ignite group_id (my-super-sql/cull-semicolon f))]
                                                                                                                               (if-not (nil? rs)
                                                                                                                                   (recur ignite group_id r (conj lst-rs "true"))
                                                                                                                                   (recur ignite group_id r (conj lst-rs "false"))))
                  ; create table
                  (and (string? (first f)) (my-lexical/is-eq? (first f) "create") (my-lexical/is-eq? (second f) "table")) (let [rs (my-create-table/my_create_table_lst ignite group_id (my-super-sql/cull-semicolon f))]
                                                                                                                                (if (nil? rs)
                                                                                                                                    (recur ignite group_id r (conj lst-rs "true"))
                                                                                                                                    (recur ignite group_id r (conj lst-rs "false"))
                                                                                                                                    ))
                  ; alter table
                  (and (string? (first f)) (my-lexical/is-eq? (first f) "ALTER") (my-lexical/is-eq? (second f) "table")) (let [rs (my-alter-table/alter_table ignite group_id (str/join " " (my-super-sql/cull-semicolon f)))]
                                                                                                                               (if (nil? rs)
                                                                                                                                   (recur ignite group_id r (conj lst-rs "true"))
                                                                                                                                   (recur ignite group_id r (conj lst-rs "false"))
                                                                                                                                   ))
                  ; drop table
                  (and (string? (first f)) (my-lexical/is-eq? (first f) "DROP") (my-lexical/is-eq? (second f) "table")) (let [rs (my-drop-table/drop_table ignite group_id (str/join " " (my-super-sql/cull-semicolon f)))]
                                                                                                                              (if (nil? rs)
                                                                                                                                  (recur ignite group_id r (conj lst-rs "true"))
                                                                                                                                  (recur ignite group_id r (conj lst-rs "false"))
                                                                                                                                  ))
                  ; create index
                  (and (string? (first f)) (my-lexical/is-eq? (first f) "create") (my-lexical/is-eq? (second f) "INDEX")) (let [rs (my-create-index/create_index ignite group_id (str/join " " (my-super-sql/cull-semicolon f)))]
                                                                                                                                (if (nil? rs)
                                                                                                                                    (recur ignite group_id r (conj lst-rs "true"))
                                                                                                                                    (recur ignite group_id r (conj lst-rs "false"))
                                                                                                                                    ))
                  ; drop index
                  (and (string? (first f)) (my-lexical/is-eq? (first f) "DROP") (my-lexical/is-eq? (second f) "INDEX")) (let [rs (my-drop-index/drop_index ignite group_id (str/join " " (my-super-sql/cull-semicolon f)))]
                                                                                                                              (if (nil? rs)
                                                                                                                                  (recur ignite group_id r (conj lst-rs "true"))
                                                                                                                                  (recur ignite group_id r (conj lst-rs "false"))
                                                                                                                                  ))
                  ; no sql
                  ;(contains? #{"no_sql_create" "no_sql_insert" "no_sql_update" "no_sql_delete" "no_sql_query" "no_sql_drop" "push" "pop"} (str/lower-case (first f))) (.append sb (str (my-super-cache/my-no-lst ignite group_id lst (str/join " " lst)) ";"))
                  (and (string? (first f)) (contains? #{"noSqlInsert" "noSqlUpdate" "noSqlDelete" "noSqlDrop"} (str/lower-case (first f)))) (let [my-code (my-smart-clj/token-to-clj ignite group_id (my-select/sql-to-ast (my-super-sql/cull-semicolon f)) nil)]
                                                                                                                                                (recur ignite group_id r (conj lst-rs (str (eval (read-string my-code)))))
                                                                                                                                                )
                  (and (string? (first f)) (my-lexical/is-eq? (first f) "show_train_data")) (if-let [show-sql (my-super-sql/call-show-train-data ignite group_id (my-super-sql/cull-semicolon f))]
                                                                                                (recur ignite group_id r (conj lst-rs (format "select show_train_data(%s) as tip;" show-sql))))
                  :else
                  (if (string? (first f))
                      (let [smart-sql-obj (my-super-sql/my-smart-sql ignite group_id f)]
                          (if (map? smart-sql-obj)
                              (recur ignite group_id r (conj lst-rs (-> smart-sql-obj :sql)))
                              (recur ignite group_id r (conj lst-rs smart-sql-obj))))
                      (let [smart-sql-obj (my-super-sql/my-smart-sql ignite group_id (apply concat f))]
                          (if (map? smart-sql-obj)
                              (recur ignite group_id r (conj lst-rs (-> smart-sql-obj :sql)))
                              (recur ignite group_id r (conj lst-rs smart-sql-obj))))
                      )
                  ;(throw (Exception. "输入字符有错误！不能解析，请确认输入正确！"))
                  ))))

(defn execute-sql-query [^String userToken ^String sql ^String ps]
    (if-let [group_id (my-user-group/get_user_group (Ignition/ignite) userToken) lst (my-smart-sql/re-super-smart-segment (my-smart-sql/get-my-smart-segment sql))]
        (execute-sql-query-lst (Ignition/ignite) group_id lst [] ps)))

(defn -executeSqlQuery [this ^String userToken ^String sql ^String ps]
    (execute-sql-query userToken sql ps))



































