package ru.synesis.kipod.facematch.ignite;
/* Description: ru.synesis.http/IgniteSearchQuery
 *
 * Copyright (c) 2017 Synesis LLC.
 * Author Sergey Dobrodey <sergey.dobrodey@synesis.ru>, Synesis LLC www.synesis.ru.
 */

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.ignite.IgniteCompute;
import org.apache.ignite.lang.IgniteCallable;
import org.apache.ignite.resources.ServiceResource;

import ru.synesis.kipod.event.FaceKipodEvent;

public class IgniteSearchQuery {

    private IgniteCompute compute;
    private QueryFilter queryFilter;

    public IgniteSearchQuery(IgniteCompute compute, QueryFilter filter) {
        this.compute = compute;
        this.queryFilter = filter;
    }

    public Collection<Collection<Map.Entry<FaceKipodEvent, Float>>> execute(List<float[]> descriptors, float minSimilarity) {
        return compute.broadcast(new IgniteCallable<Collection<Map.Entry<FaceKipodEvent, Float>>>() {
            private static final long serialVersionUID = 1L;

            @ServiceResource(serviceName = "recognitionService")
            private RecognitionService recognitionService;

            @Override
            public Collection<Map.Entry<FaceKipodEvent, Float>> call() throws Exception {
                return recognitionService.findSimilarParallel(descriptors, minSimilarity, queryFilter);
            }
        });
    }

}
