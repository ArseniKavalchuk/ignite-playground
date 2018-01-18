package ru.synesis.kipod.event;

import java.util.List;

import com.google.gson.JsonElement;

// differs from KipodEvent by ID value, this one contains ID as channel:start_time:id
public class UIKipodEvent extends AbstractEvent {
    
    private static final long serialVersionUID = 1490848614921986172L;

    public String id;
    public Long start_time;
    public Long end_time;
    public List<JsonElement> snapshots;
    public Long commented_at;
    public Long processed_at;
    public Long offset;
    
    // FIXME : remove this bullshit!
    public float similarity;
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        UIKipodEvent other = (UIKipodEvent) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }
    
    public static int compareStartTimeAsc(UIKipodEvent o1, UIKipodEvent o2) {
        return (int) (o1.start_time - o2.start_time);
    }
    
    public static int compareStartTimeDesc(UIKipodEvent o1, UIKipodEvent o2) {
        return (int) (o2.start_time - o1.start_time);
    }

    public static int compareSimilarity(UIKipodEvent o1, UIKipodEvent o2) {
        if (o1.similarity == o2.similarity) {
            return 0;
        } else if (o1.similarity > o2.similarity) {
            return 1;
        } else {
            return -1;
        }
    }

}
