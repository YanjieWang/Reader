package reader.a6pu.com.reader.utils;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by 王燕杰 on 2016/5/26.
 */
public class DbUtilsBookDate {
    private static  SQLiteDatabase sqlitedb;
    public SQLiteDatabase getDatebase() {
        if(sqlitedb!=null&&sqlitedb.isOpen()){
            return sqlitedb;
        }
        File path = new File(Environment.getExternalStorageDirectory(), "myApp/myDbBook"); //数据库文件目录\

        if (!path.exists()) {
            path.mkdirs();//创建目录
        }
        path = new File(path, "myDbBook.db");//数据库文件
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
            sqlitedb.query("jsonContent", null, null, null, null, null, null);
        } catch (Exception e) {
            e.printStackTrace();
            //表不存在时会报异常，异常时创建表
            sqlitedb.execSQL("create table jsonContent (id integer primary key autoincrement,keyword varchar,typeId varchar,page varchar,bookId varchar,chapterId varchar,jsonContent BLOB  not null)");
        }

        return sqlitedb;
    }

    public void closeDataBase(){
        if (sqlitedb!=null){
            sqlitedb.close();
            sqlitedb=null;
        }
    }

    //清除所有缓存数据
    public void EarseAllBookData() {
        File path = new File(Environment.getExternalStorageDirectory(), "myApp/myDbBook/myDbBook.db"); //BooKs数据库文件
        if(path.exists()){
            path.delete();
        }
    }


    //存储BookTypeJson
    public void insertBookTypeJsonContent(String jsonContent){
        getDatebase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("jsonContent",jsonContent.getBytes());
        sqlitedb.insert("jsonContent",null,contentValues);
        closeDataBase();
    }

    //存储BookJson
    public void insertBookJsonContent(String keyword,String typeId,String page,String jsonContent){
        getDatebase();
        //(id integer primary key autoincrement,keyword varchar,typeId varchar,page varchar,bookId varchar,chapterId varchar,jsonContent BLOB  not null)
        ContentValues contentValues = new ContentValues();
        contentValues.put("keyword",keyword);
        contentValues.put("typeId",typeId);
        contentValues.put("page",page);
        contentValues.put("jsonContent",jsonContent.getBytes());
        sqlitedb.insert("jsonContent", null, contentValues);
        closeDataBase();
    }
    //存储BookChapterJson
    public void insertBookChapterJsonContent(String bookId,String jsonContent){
        getDatebase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("bookId",bookId);
        contentValues.put("jsonContent",jsonContent.getBytes());
        sqlitedb.insert("jsonContent",null,contentValues);
        closeDataBase();
    }

    //存储BookContentJson
    public void insertBookContentJsonContent(String bookId, String chapterId,String jsonContent){
        getDatebase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("bookId",bookId);
        contentValues.put("chapterId",chapterId);
        contentValues.put("jsonContent",jsonContent.getBytes());
        sqlitedb.insert("jsonContent",null,contentValues);
        closeDataBase();
    }

    //获取BookTypeJson
    public String getBookTypeJsonContent(){
        getDatebase();
        String res=null;
        String []culms={"jsonContent"};
        String selection="keyword is null and typeId is null and page is null and bookId is null and chapterId is null";
        Cursor cursor = sqlitedb.query("jsonContent", culms, selection, null, null, null, null);
        if(cursor.getCount()>=1){
            cursor.moveToFirst();
            res=new String(cursor.getBlob(cursor.getColumnIndex("jsonContent")));
        }
        closeDataBase();
        return res;
    }



    //获取BookJson
    public String getBookJsonContent(String keyword,String typeId,String page){
        getDatebase();
        String res=null;
        String []culms={"jsonContent"};
        String [] args={keyword,typeId,page};
        String selection="keyword=? and typeId=? and page=? and bookId is null and chapterId is null";
        Cursor cursor = sqlitedb.query("jsonContent", culms, selection, args, null, null, null);
        if(cursor.getCount()>=1){
            cursor.moveToFirst();
            res=new String(cursor.getBlob(cursor.getColumnIndex("jsonContent")));
        }
        closeDataBase();
        return res;
    }

    //获取BookJson
    public String getBookChapterJsonContent(String bookId){
        getDatebase();
        String res=null;
        String []culms={"jsonContent"};
        String [] args={bookId};
        String selection="keyword is null and typeId is null and page is null and bookId=? and chapterId is null";
        Cursor cursor = sqlitedb.query("jsonContent", culms, selection, args, null, null, null);
        if(cursor.getCount()>=1){
            cursor.moveToFirst();
            res=new String(cursor.getBlob(cursor.getColumnIndex("jsonContent")));
        }
        closeDataBase();
        return res;
    }


    //获取BookJson
    public String getBookContentJsonContent(String bookId, String chapterId){
        getDatebase();
        String res=null;
        String []culms={"jsonContent"};
        String [] args={bookId,chapterId};
        String selection="keyword is null and typeId is null and page is null and bookId=? and chapterId=?";
        Cursor cursor = sqlitedb.query("jsonContent", culms, selection, args, null, null, null);
        if(cursor.getCount()>=1){
            cursor.moveToFirst();
            res=new String(cursor.getBlob(cursor.getColumnIndex("jsonContent")));
        }
        closeDataBase();
        return res;
    }


}
