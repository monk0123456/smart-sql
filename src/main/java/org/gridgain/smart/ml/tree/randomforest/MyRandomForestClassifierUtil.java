package org.gridgain.smart.ml.tree.randomforest;

import org.apache.commons.math3.util.Precision;
import org.apache.ignite.Ignite;
import org.apache.ignite.cache.query.QueryCursor;
import org.apache.ignite.cache.query.ScanQuery;
import org.apache.ignite.ml.dataset.feature.extractor.Vectorizer;
import org.apache.ignite.ml.dataset.feature.extractor.impl.DummyVectorizer;
import org.apache.ignite.ml.math.primitives.vector.Vector;
import org.apache.ignite.ml.tree.DecisionTreeClassificationTrainer;
import org.apache.ignite.ml.tree.DecisionTreeNode;
import org.apache.ignite.ml.tree.randomforest.RandomForestClassifierTrainer;
import org.gridgain.smart.ml.model.MyMLMethodName;
import org.gridgain.smart.ml.model.MyMModelKey;
import org.gridgain.smart.ml.model.MyMlModel;

import javax.cache.Cache;

public class MyRandomForestClassifierUtil {

    /**
     * 获取 model
     * */
//    public static void getMdlToCache(final Ignite ignite, final String cacheName, final int maxDeep, final double minImpurityDecrease)
//    {
//        RandomForestClassifierTrainer trainer = new RandomForestClassifierTrainer();
//
//        Vectorizer<Long, Vector, Integer, Double> vectorizer = new DummyVectorizer<Long>()
//                .labeled(Vectorizer.LabelCoordinate.FIRST);
//        //TrainTestSplit<Long, Vector> split = new TrainTestDatasetSplitter<Long, Vector>().split(0.8);
//
////        DecisionTreeNode model = trainer.fit(
////                ignite, ignite.cache(cacheName),
////                vectorizer
////        );
//
//        double score = 0D;
//        int correctPredictions = 0;
//        int count = 0;
//        try (QueryCursor<Cache.Entry<Long, Vector>> observations = ignite.cache(cacheName).query(new ScanQuery<>()))
//        {
//            for (Cache.Entry<Long, Vector> observation : observations) {
//                Vector val = observation.getValue();
//                Vector inputs = val.copyOfRange(1, val.size());
//                double label = val.copyOfRange(0, 1).get(0);
//                double prediction = model.predict(inputs);
//
//                if (Precision.equals(prediction, label, Precision.EPSILON)) {
//                    correctPredictions++;
//                }
//                count++;
//            }
//            score = correctPredictions / count;
//        }
//
//        if (model != null)
//        {
//            ignite.cache("my_ml_model").put(new MyMModelKey(cacheName, MyMLMethodName.DecisionTreeClassification), new MyMlModel(model, score));
//        }
//    }
}
