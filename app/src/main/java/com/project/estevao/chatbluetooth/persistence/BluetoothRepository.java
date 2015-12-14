package com.project.estevao.chatbluetooth.persistence;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.project.estevao.chatbluetooth.entities.User;

import java.util.List;

public class BluetoothRepository {

    public static void save(User user) {
        DatabaseHelper databaseHelper = DatabaseHelper.getInstance();
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        ContentValues values = BluetoothContract.getContentValues(user);

        if (user.getId() == null) {
            db.insert(BluetoothContract.TABLE, null, values);
        } else {
            String where = BluetoothContract.ID + " = ?";
            String[] params = {user.getId().toString()};
            db.update(BluetoothContract.TABLE, values, where, params);
        }
        db.close();
        databaseHelper.close();
    }

    public static User getUser() {
        DatabaseHelper databaseHelper = DatabaseHelper.getInstance();
        SQLiteDatabase db = databaseHelper.getReadableDatabase();

        Cursor cursor = db.query(BluetoothContract.TABLE, BluetoothContract.COLUNS, null, null, null, null, BluetoothContract.ID);
        User values = BluetoothContract.getUser(cursor);

        db.close();
        databaseHelper.close();
        return values;
    }

    public static void delete(long id) {
        DatabaseHelper databaseHelper = DatabaseHelper.getInstance();
        SQLiteDatabase db = databaseHelper.getReadableDatabase();

        String where = BluetoothContract.ID + " = ? ";
        String[] params = {String.valueOf(id)};
        db.delete(BluetoothContract.TABLE, where, params);

        db.close();
        databaseHelper.close();
    }

//    public static List<Notes> findByFilterItens(int nBanheiros, int nQuartos, int ehVenda, int ehAluguel, Double preco) {
//        DatabaseHelper databaseHelper = DatabaseHelper.getInstance();
//        SQLiteDatabase db = databaseHelper.getReadableDatabase();
//
//        String where = BluetoothContract.NBANHEIROS + " >= ? AND " + BluetoothContract.NQUARTOS + " >= ? AND " +
//                BluetoothContract.EHVENDA + " = ? AND " + BluetoothContract.EHALUGUEL + " = ? AND " + BluetoothContract.PRECO + " <= ? ;";
//
//        String[] params = {String.valueOf(nBanheiros), String.valueOf(nQuartos), String.valueOf(ehVenda), String.valueOf(ehAluguel), String.valueOf(preco)};
//
//        Cursor cursor = db.query(BluetoothContract.TABLE, BluetoothContract.COLUNS, where, params, null, null, null);
//        List<House> values = BluetoothContract.getHouses(cursor);
//
//        db.close();
//        databaseHelper.close();
//        return values;
//    }
}
