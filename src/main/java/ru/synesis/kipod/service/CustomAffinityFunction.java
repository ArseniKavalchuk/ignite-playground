package ru.synesis.kipod.service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.ignite.cache.affinity.AffinityFunction;
import org.apache.ignite.cache.affinity.AffinityFunctionContext;
import org.apache.ignite.cluster.ClusterNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CustomAffinityFunction implements AffinityFunction {

    private static final Logger LOG = LoggerFactory.getLogger(CustomAffinityFunction.class);
    
    private static final long serialVersionUID = 1L;

    private int partitions = 8;
    
    @Override
    public void reset() {
        if (LOG.isDebugEnabled()) LOG.debug("Reset topology is invoked");
    }

    @Override
    public int partitions() {
        if (LOG.isDebugEnabled()) LOG.debug("Get partitions count");
        return partitions;
    }

    @Override
    public int partition(Object key) {
        int partition = 0;
        if (key instanceof String) {
            partition = fromEventId((String) key);
        } else if (key instanceof Number) {
            partition = fromTimestamp(((Number) key).longValue());
        } else {
            if (LOG.isWarnEnabled()) LOG.warn("Key type {} is not supported", key);
        }
        // if (LOG.isDebugEnabled()) LOG.debug("key {} -> partition {}", key, partition);
        return partition;
    }

    @Override
    public List<List<ClusterNode>> assignPartitions(AffinityFunctionContext affCtx) {
        if (LOG.isDebugEnabled()) LOG.debug("Assign partitions, ctx = {}", affCtx);
        List<ClusterNode> nodes = affCtx.currentTopologySnapshot();
        if (LOG.isDebugEnabled()) LOG.debug("Topology: {}", nodes);
        // result partition to List<ClusterNode>
        List<List<ClusterNode>> result = new ArrayList<>();
        for (int i = 0; i < partitions; i++) result.add(new ArrayList<>());
        
        // Note that partitioned affinity must obey the following contract:
        // given that node N is primary for some key K, if any other node(s)
        // leave grid and no node joins grid, node N will remain primary for key K.
        
        // distribute nodes by consistentId's hash 
        int nodesCount = nodes.size();
        // ClusterNode[] nodesByConsistentIdHash = new ClusterNode[nodes.size()];
        //for (ClusterNode node : nodes) {
        //    int idx = node.consistentId().hashCode() % nodesCount;
        //}
        for (int i = 0; i < partitions; i++) {
            int nodeIdx = i % nodesCount;
            ClusterNode node = nodes.get(nodeIdx);
            result.get(i).add(node);
        }
        return result;
    }

    @Override
    public void removeNode(UUID nodeId) {
        if (LOG.isDebugEnabled()) LOG.debug("Remove node {}", nodeId);
    }
    
    private int fromTimestamp(long longValue) {
        return lastMinuteFromMinutes(longValue) % partitions;
    }

    private int fromEventId(String stringValue) {
        String[] parts = stringValue.split(":");
        if (parts.length >= 3) {
            return fromTimestamp(Long.valueOf(parts[1]));
        }
        return 0;
    }
    
    private static int minutesOfHour(long timestamp) {
        return (int) (((timestamp / 1000) / 60) % 60);
    }

    private static int lastMinuteFromMinutes(long timestamp) {
        return minutesOfHour(timestamp) % 10;
    }

    public static void main(String[] args) throws InterruptedException {
        for (;;) {
            long ts = System.currentTimeMillis();
            int aMinute1 = minutesOfHour(ts);
            int aMinute2 = lastMinuteFromMinutes(ts);
            LOG.debug("M1 {} part {}, M2 {} part {}", aMinute1, aMinute1 % 8, aMinute2, aMinute2 % 8);
            Thread.sleep(1000);
        }
    }

}
