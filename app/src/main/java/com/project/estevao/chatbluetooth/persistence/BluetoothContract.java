package com.project.estevao.chatbluetooth.persistence;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;


import com.project.estevao.chatbluetooth.entities.User;

import java.util.ArrayList;
import java.util.List;

public class BluetoothContract {

    public static String TABLE = "User";
    public static String ID = "id";
    public static String LOGIN = "login";
    public static String PHOTO = "photo";

    public static final String[] COLUNS = {ID, LOGIN, PHOTO};

    public static String getCreateTableScript() {
        final StringBuilder create = new StringBuilder();

        create.append(" CREATE TABLE " + TABLE);
        create.append(" ( ");
        create.append(ID + " INTEGER PRIMARY KEY, ");
        create.append(LOGIN + " TEXT, ");
        create.append(PHOTO + " BLOB ");
        create.append(" ); ");
        return create.toString();
    }

    public static ContentValues getContentValues(User user) {
        ContentValues values = new ContentValues();
        values.put(BluetoothContract.ID, user.getId());
        values.put(BluetoothContract.LOGIN, user.getLogin());
        values.put(BluetoothContract.PHOTO, user.getPhoto());
        return values;
    }

    public static User getUser(Cursor cursor) {
        User user = new User();
        if (!cursor.isBeforeFirst() || cursor.moveToNext()) {
            user.setId(cursor.getLong(cursor.getColumnIndex(BluetoothContract.ID)));
            user.setLogin(cursor.getString(cursor.getColumnIndex(BluetoothContract.LOGIN)) == "true" ? true : false);
            user.setPhoto(cursor.getBlob(cursor.getColumnIndex(BluetoothContract.PHOTO)));
            return user;
        }
        return null;
    }
}
