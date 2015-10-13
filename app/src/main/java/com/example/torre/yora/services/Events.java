package com.example.torre.yora.services;


public final class Events
{
    private Events()
    {
    }

    public static final int OPERATION_CREATED = 0;
    public static final int OPERATION_DELETED = 1;

    public static final int ENTITY_CONTACT_REQUEST = 1;
    public static final int ENTITIY_CONTACT = 2;
    public static final int ENTITY_MESSAGE = 3;

    public static class OnNotificationReceivedEvent
    {
        public int operationType;
        public int entityType;
        public int entityId;
        public int entityOwnerId;
        public String entityOwnerName;

        public OnNotificationReceivedEvent(int operationType, int entityType, int entityId, int entityOwnerId, String entityOwnerName)
        {
            this.operationType = operationType;
            this.entityType = entityType;
            this.entityId = entityId;
            this.entityOwnerId = entityOwnerId;
            this.entityOwnerName = entityOwnerName;
        }
    }
}
