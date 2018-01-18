package ru.synesis.kipod.event;

public class EventAttribute {

    private String name;
    private String value;
    private Float similarity;

    public EventAttribute(String name, String value, Float similarity) {
        this.name = name;
        this.value = value;
        this.similarity = similarity;
    }

	public String getName() {
		return name;
	}

	public String getValue() {
		return value;
	}

	public Float getSimilarity() {
		return similarity;
	}

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((similarity == null) ? 0 : similarity.hashCode());
        result = prime * result + ((value == null) ? 0 : value.hashCode());
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
        EventAttribute other = (EventAttribute) obj;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (similarity == null) {
            if (other.similarity != null)
                return false;
        } else if (!similarity.equals(other.similarity))
            return false;
        if (value == null) {
            if (other.value != null)
                return false;
        } else if (!value.equals(other.value))
            return false;
        return true;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("EventAttribute [name=");
        builder.append(name);
        builder.append(", value=");
        builder.append(value);
        builder.append(", similarity=");
        builder.append(similarity);
        builder.append("]");
        return builder.toString();
    }

}