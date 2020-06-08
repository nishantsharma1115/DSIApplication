package com.application.dsi.common;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class Constants {
    public static final DatabaseReference DB = FirebaseDatabase.getInstance().getReference();
    public static final StorageReference SR = FirebaseStorage.getInstance().getReference();

    public static final int OPERATION_IN_PROGRESS = 0;
    public static final int OPERATION_COMPLETE_SUCCESS = 1;
    public static final int OPERATION_COMPLETE_FAILURE = -1;
}
