package reader.a6pu.com.reader.utils;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

import java.io.File;
import java.io.IOException;

/**
 * Created by 王燕杰 on 2016/5/26.
 */
public class DbUtilsExtenel {
    private static  SQLiteDatabase sqlitedb;
    public SQLiteDatabase getDatebase() {
        if(sqlitedb!=null&&sqlitedb.isOpen()){
            return sqlitedb;
        }
        File path = new File(Environment.getExternalStorageDirectory(), "myApp/myDb"); //数据库文件目录\

        if (!path.exists()) {
            path.mkdirs();//创建目录
        }
        path = new File(path, "myAppDb.db");//数据库文件
        //----如要在SD卡中创建数据库文件，先做如下的判断和创建相对应的目录和文件----
        if (!path.exists()) {      //判断文件是否存在
            try {
                path.createNewFile();  //创建文件
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        sqlitedb = SQLiteDatabase.openOrCreateDatabase(path, null);
        //创建用户表
        try {
            sqlitedb.query("userInfo", null, null, null, null, null, null);
        } catch (Exception e) {
            e.printStackTrace();
            //表不存在时会报异常，异常时创建表
            sqlitedb.execSQL("create table userInfo (id integer primary key autoincrement,uName varchar not null,uPsw varchar not null)");
        }

        return sqlitedb;
    }


    public void insertUser(String uName,String uPsw){
        SQLiteDatabase sqlitedb = getDatebase();
        ContentValues values=new ContentValues();
        values.put("uName",uName);
        values.put("uPsw",uPsw);
        sqlitedb.insert("userInfo", null, values);
        closeDataBase();

    }

    public void closeDataBase(){
        if (sqlitedb!=null){
            sqlitedb.close();
        }
    }
    public Cursor QueryUser(String uName,String uPsw){
        SQLiteDatabase sqlitedb = getDatebase();
        ContentValues values=new ContentValues();
        String[] str={uName,uPsw};
        Cursor cursor = sqlitedb.query("userInfo", null, "uName=? and uPsw=?", str, null, null, null);
        return cursor;
    }

    public boolean isReg(String uName) {
        SQLiteDatabase sqlitedb = getDatebase();
        ContentValues values=new ContentValues();
        String[] str={uName};
        Cursor cursor = sqlitedb.query("userInfo", null, "uName=?", str, null, null, null);
        if(cursor==null||cursor.getCount()==0){
            closeDataBase();
            return false;
        }else{
            closeDataBase();
            return true;
        }
    }
}
