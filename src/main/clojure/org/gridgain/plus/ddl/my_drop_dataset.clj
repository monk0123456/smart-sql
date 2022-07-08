(ns org.gridgain.plus.ddl.my-drop-dataset
    (:require
        [org.gridgain.plus.dml.select-lexical :as my-lexical]
        [clojure.core.reducers :as r]
        [clojure.string :as str])
    (:import (org.apache.ignite Ignite IgniteCache)
             (org.apache.ignite.configuration CacheConfiguration)
             (org.apache.ignite.cache.query FieldsQueryCursor SqlFieldsQuery)
             (cn.myservice MyInitFuncService)
             )
    (:gen-class
        ; 生成 class 的类名
        :name org.gridgain.plus.dml.MyDropDataSet
        ; 是否生成 class 的 main 方法
        :main false
        ; 生成 java 静态的方法
        ;:methods [^:static [getPlusInsert [org.apache.ignite.Ignite Long String] clojure.lang.PersistentArrayMap]]
        ))

(defn get-dataset-name [^String sql]
    (let [lst (my-lexical/to-back (str/lower-case sql))]
        (cond (and (= (count lst) 5) (= '("drop" "dataset" "if" "exists") (take 4 lst))) (last lst)
              (and (= (count lst) 3) (= '("drop" "dataset") (take 2 lst))) (last lst)
              :else
              (throw (Exception. "输入字符串错误！"))
              )))

(defn get-dataset-name-lst [lst]
    (cond (and (= (count lst) 5) (= '("drop" "dataset" "if" "exists") (take 4 lst))) (last lst)
          (and (= (count lst) 3) (= '("drop" "dataset") (take 2 lst))) (last lst)
          :else
          (throw (Exception. "输入字符串错误！"))
          ))

; 删除 dataset
(defn drop-data-set [^Ignite ignite ^Long group-id ^String sql]
    (if (= group-id 0)
        (if-let [data_set_name (get-dataset-name sql)]
            (if-let [ds-cache (.cache ignite (str (str/lower-case data_set_name) "_meta"))]
                (if (empty? (.getAll (.query (.cache ignite "my_dataset") (.setArgs (SqlFieldsQuery. "select mt.id from my_dataset as m, my_meta_tables as mt where m.id = mt.data_set_id and m.dataset_name = ? limit 0, 1") (to-array [(str/lower-case data_set_name)])))))
                    (do
                        (.dropSchemaFunc (.getInitFunc (MyInitFuncService/getInstance)) ignite (str/lower-case data_set_name))
                        (.destroy ds-cache)
                        (.getAll (.query (.cache ignite "my_dataset") (.setArgs (SqlFieldsQuery. "delete from my_dataset where dataset_name = ?") (to-array [(str/lower-case data_set_name)])))))
                    (throw (Exception. "数据集中还存在表！不能删除！")))))
        (throw (Exception. "没有执行语句的权限！"))))

(defn drop-data-set-lst [^Ignite ignite ^Long group-id lst]
    (if (= group-id 0)
        (if-let [data_set_name (get-dataset-name-lst lst)]
            (if-let [ds-cache (.cache ignite (str (str/lower-case data_set_name) "_meta"))]
                (if (empty? (.getAll (.query (.cache ignite "my_dataset") (.setArgs (SqlFieldsQuery. "select mt.id from my_dataset as m, my_meta_tables as mt where m.id = mt.data_set_id and m.dataset_name = ? limit 0, 1") (to-array [(str/lower-case data_set_name)])))))
                    (do
                        (.dropSchemaFunc (.getInitFunc (MyInitFuncService/getInstance)) ignite (str/lower-case data_set_name))
                        (.destroy ds-cache)
                        (.getAll (.query (.cache ignite "my_dataset") (.setArgs (SqlFieldsQuery. "delete from my_dataset where dataset_name = ?") (to-array [(str/lower-case data_set_name)])))))
                    (throw (Exception. "数据集中还存在表！不能删除！")))))
        (throw (Exception. "没有执行语句的权限！"))))
