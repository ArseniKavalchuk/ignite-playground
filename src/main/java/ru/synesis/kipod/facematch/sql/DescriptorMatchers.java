package ru.synesis.kipod.facematch.sql;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.ignite.cache.query.annotations.QuerySqlFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DescriptorMatchers implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private static final Logger LOG = LoggerFactory.getLogger(DescriptorMatchers.class);

    public final static String VERSION_0_0_0    = "0.0.0";
    public final static String VERSION_5_5_63   = "5.5.63";
    public final static String DEFAULT_VERSION  = VERSION_5_5_63;
    
    @QuerySqlFunction
    public static float matchDescriptors(
            String descriptorVersion,
            List<float[]> fieldDescriptors,
            Map<String, List<float[]>> queryDescriptorsMap) {
        
        if (LOG.isTraceEnabled()) {
            LOG.trace(">>>>> FUNCTION >>>>>");
            LOG.trace("descriptorVersion = {}", descriptorVersion);
            LOG.trace("fieldDescriptors = {}", fieldDescriptors);
            LOG.trace("queryDescriptors = {}", queryDescriptorsMap);
        }
        List<float[]> queryDescriptors = selectDescriptors(descriptorVersion, queryDescriptorsMap);
       
        if (queryDescriptors == null) {
            LOG.trace("No matching version in descripto map");
            return Float.MAX_VALUE;
        }
        
        return minDistance(fieldDescriptors, queryDescriptors);
    }
    
    public static List<float[]> selectDescriptors(String descriptorVersion, Map<String, List<float[]>> queryDescriptorsMap) {
        List<float[]> queryDescriptors = null;
        if (descriptorVersion == null) {
            if (queryDescriptorsMap.containsKey(DEFAULT_VERSION)) {
                queryDescriptors = queryDescriptorsMap.get(DEFAULT_VERSION);
            } else {
                LOG.trace("Field descriptor_version is null");
            }
        } else {
            queryDescriptors = queryDescriptorsMap.get(descriptorVersion);
        }
        return queryDescriptors;
    }

    public static float minDistance(List<float[]> fieldDescriptors, List<float[]> queryDescriptors) {
        float minDistance = Float.MAX_VALUE;
        for (float[] descriptor1 : fieldDescriptors) {
            for (float[] descriptor2 : queryDescriptors) {
                float dst = distance(descriptor1, descriptor2);
                minDistance = Math.min(minDistance, dst);
            }
        }
        if (LOG.isTraceEnabled()) {
            LOG.trace("Distance: {}", minDistance);
        }
        return minDistance;
    }
    
    private static float distance(float[] one, float[] two) {
        if (LOG.isTraceEnabled()) {
            LOG.trace("one -> {}", one != null ? Arrays.toString(one) : "null");
            LOG.trace("two -> {}", two != null ? Arrays.toString(two) : "null");
        }
        if (one == null || two == null || one.length != two.length) return 0f;
        float distance = 0f;
        float t;
        for (int i = 0; i < one.length; i++) {
            t = two[i] - one[i];
            distance += t * t;
        }
        return distance;
    }
    
}
