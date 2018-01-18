package ru.synesis.kipod.event;

import java.io.Serializable;

/**
 * 
 * @author arseny kovalchuk
 *
 */
public class FaceDetectedParams implements Serializable {

    private static final long serialVersionUID = -3452353869127478820L;

    public int alg_type;
    public int major_version;
    public int minor_version;
    public FaceDescriptor[] descriptors;
    
    public static class FaceDescriptor implements Serializable {
        
        private static final long serialVersionUID = 7578640455459334120L;
        
        public FaceDescriptor() {}
        
        public FaceDescriptor(String data, String path) {
            this.data = data;
            this.path = path;
        }
        
        public String data;
        public String path;

    }

}
