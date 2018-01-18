package ru.synesis.kipod.event;
/**
 *
 * @author arseny.kovalchuk
 *
 */
public final class EventConstants {

    private EventConstants() {}

    public static final String KIPOD_EVENTS     = "kipod_events";
    public static final String FACE_EVENTS      = "face_events";

    public static final String MOD_FACE                 = "Kpx.Synesis.Faces";
    public static final String MOD_NUMBER               = "Kpx.Synesis.NumberTrack";
    public static final String T_FACE_DETECTED          = "FaceDetected";
    public static final String T_FACE_MATCHED           = "FaceMatched";
    public static final String T_FACE_NOT_MATCHED       = "FaceNotMatched";
    public static final String T_EVENT_SYNC_POINT       = "EventSyncPoint";
    public static final String T_OUT_OF_SYNC            = "OutOfSync";
    public static final String T_PLATE_DETECTED         = "PlateDetected";
    public static final String T_PLATE_MATCHED          = "PlateMatched";
    public static final String T_PLATE_NOT_MATCHED      = "PlateNotMatched";

    public static final String F_ID                 = "id";
    public static final String F_NAME               = "name";
    public static final String F_TOPIC              = "topic";
    public static final String F_MODULE             = "module";
    public static final String F_SNAPSHOTS          = "snapshots";
    public static final String F_PARAMS             = "params";
    public static final String F_TAGS               = "tags";
    public static final String F_START_TIME         = "start_time";
    public static final String F_END_TIME           = "end_time";
    public static final String F_PLATE              = "plate";
    public static final String F_STATE              = "state";
    public static final String F_NUMBER             = "number";
    public static final String F_IDENTITY           = "identity";
    public static final String F_STATUS             = "status";
    public static final String F_OWNER              = "owner";
    public static final String F_LISTS              = "lists";
    public static final String F_DESCRIPTORS        = "descriptors";
    public static final String F_KAFKA_OFFSET       = "kafka_offset";
    public static final String F_DATA               = "data";
    public static final String F_CHANNEL            = "channel";
    public static final String F_LIST               = "list";
    public static final String F_FACES              = "faces";
    public static final String F_FIRST_NAME         = "first_name";
    public static final String F_LAST_NAME          = "last_name";
    public static final String F_ATTRIBUTES         = "attributes";
    public static final String F_SIMILARITY         = "similarity";
    public static final String F_DETECTED_FACE_ID   = "detected_face_id";
    public static final String F_TIME               = "time";
    public static final String F_LEVEL              = "level";
    public static final String F_SOURCE             = "source";
    public static final String F_STREAM             = "stream";
    public static final String F_ALARM              = "alarm";
    public static final String F_PROCESSED          = "processed";
    public static final String F_COMMENT            = "comment";
    public static final String F_ARMED              = "armed";
    public static final String F_COMMENTED_AT       = "commented_at";
    public static final String F_PROCESSED_AT       = "processed_at";
    public static final String F_OFFSET             = "offset";
    
    public static final String F_LICENSE_PLATE_COUNTRY          = "license_plate_country";
    public static final String F_LICENSE_PLATE_NUMBER           = "license_plate_number";
    public static final String F_LICENSE_PLATE_LISTS            = "license_plate_lists";
    public static final String F_LICENSE_PLATE_FIRST_NAME       = "license_plate_first_name";
    public static final String F_LICENSE_PLATE_LAST_NAME        = "license_plate_last_name";
    public static final String F_FACE_FIRST_NAME                = "face_first_name";
    public static final String F_FACE_LAST_NAME                 = "face_last_name";
    public static final String F_FACE_LIST_ID                   = "face_list_id";

}
