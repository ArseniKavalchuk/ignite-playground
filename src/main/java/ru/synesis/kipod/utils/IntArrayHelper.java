package ru.synesis.kipod.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class IntArrayHelper {

    private ArrayList<Integer> values = new ArrayList<>();
    
    public void setArrayCollection(List<Integer[]> collection) {
        for (Integer[] arr : collection) {
            this.setArray(arr);
        }
    }
    
    public void setArray(Integer[] array) {
        values.addAll(Arrays.asList(array));
    }
    
    public Integer[] asArray() {
        return values.toArray(new Integer[0]);
    }
}
