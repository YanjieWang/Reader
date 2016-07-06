package reader.a6pu.com.reader.utils;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import reader.a6pu.com.reader.entity.Book;
import reader.a6pu.com.reader.entity.ReadingRecord;

/**
 * Created by 王燕杰 on 2016/5/26.
 */
public class DbUtilsUserData {
    private static  SQLiteDatabase sqlitedb;
    public SQLiteDatabase getDatebase() {
        if(sqlitedb!=null&&sqlitedb.isOpen()){
            return sqlitedb;
        }
        File path = new File(Environment.getExternalStorageDirectory(), "myApp/myDbUser"); //数据库文件目录\

        if (!path.exists()) {
            path.mkdirs();//创建目录
        }
        path = new File(path, "myDbUser.db");//数据库文件
        //----如要在SD卡中创建数据库文件，先做如下的判断和创建相对应的目录和文件----
        if (!path.exists()) {      //判断文件是否存在
            try {
                path.createNewFile();  //创建文件
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //打开数据库
        sqlitedb = SQLiteDatabase.openOrCreateDatabase(path, null);
        try {
            sqlitedb.query("bookStore", null, null, null, null, null, null);
        } catch (Exception e) {
            e.printStackTrace();
            //表不存在时会报异常，异常时创建表
//
//            String author;
//            String bookId;
//            String name;
//            String newChapter;
//            String size;
//            String typeId;
//            String typeName;
//            String updateTime;
            sqlitedb.execSQL("create table bookStore (id integer primary key autoincrement," +
                    "author varchar," +
                    "bookId varchar not null unique," +
                    "name varchar," +
                    "newChapter varchar," +
                    "size varchar," +
                    "typeId varchar," +
                    "typeName varchar," +
                    "updateTime varchar)");
        }

        try {
            sqlitedb.query("readingRecords", null, null, null, null, null, null);
        } catch (Exception e) {
            e.printStackTrace();
            //表不存在时会报异常，异常时创建表
            sqlitedb.execSQL("create table readingRecords (id integer primary key autoincrement," +
                    "bookId varchar not null unique," +
                    "chapterId varchar not null," +
                    "characterIndex varchar not null)");
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
    public void EarseAllData() {
        File path = new File(Environment.getExternalStorageDirectory(), "myApp/myDbUser/myDbUser.db"); //BooKs数据库文件
        if(path.exists()){
            path.delete();
        }
    }


    //返回-1插入失败，返回-2已经存在。
    public long insertBookIntoBookStoreIfNotInserted(Book book){
        getDatebase();

        String [] args={book.getBookId()};
        String selection="bookId=?";
        Cursor cursor = sqlitedb.query("bookStore", null, selection, args, null, null, null);
        if(cursor.getCount()>0){
            return -2;
        }
        ContentValues contentValues = new ContentValues();
        contentValues.put("author",book.getAuthor());
        contentValues.put("bookId",book.getBookId());
        contentValues.put("name",book.getName());
        contentValues.put("newChapter",book.getNewChapter());
        contentValues.put("size",book.getSize());
        contentValues.put("typeId",book.getTypeId());
        contentValues.put("typeName",book.getTypeName());
        contentValues.put("updateTime",book.getUpdateTime());
        long res = sqlitedb.insert("bookStore", null, contentValues);
        closeDataBase();
        return res;
    }

    public ArrayList<Book> getBooksInBookStore(){
        getDatebase();
        ArrayList<Book> bookList=new ArrayList<Book>();
        Cursor cursor = sqlitedb.query("bookStore", null, null, null, null, null, null);
        if(cursor.getCount()>0){
            for(cursor.moveToFirst();!cursor.isAfterLast();cursor.moveToNext()){
                String author=cursor.getString(cursor.getColumnIndex("author"));
                String bookId=cursor.getString(cursor.getColumnIndex("bookId"));
                String name=cursor.getString(cursor.getColumnIndex("name"));
                String newChapter=cursor.getString(cursor.getColumnIndex("newChapter"));
                String size=cursor.getString(cursor.getColumnIndex("size"));
                String typeId=cursor.getString(cursor.getColumnIndex("typeId"));
                String typeName=cursor.getString(cursor.getColumnIndex("typeName"));
                String updateTime=cursor.getString(cursor.getColumnIndex("updateTime"));
                bookList.add(new Book(author,bookId,name,newChapter,size,typeId,typeName,updateTime));
            }
        }
        closeDataBase();
        return bookList;
    }


    //返回-1插入失败，返回-2已经存在。
    public int deleteBookFromBookStore(Book book){
        getDatebase();

        String [] args={book.getBookId()};
        String selection="bookId=?";
        int res = sqlitedb.delete("bookStore", selection, args);
        closeDataBase();
        return res;
    }



    public long insertOrReplaceBookRecord(ReadingRecord readingRecord){
        getDatebase();
        String [] args={readingRecord.getBookId()};
        String selection="bookId=?";
        Cursor cursor = sqlitedb.query("readingRecords", null, selection, args, null, null, null);
        if(cursor.getCount()>0){
            ContentValues contentValues = new ContentValues();
            contentValues.put("chapterId",readingRecord.getChapterId());;
            contentValues.put("characterIndex",readingRecord.getCharacterIndex());
            sqlitedb.update("readingRecords",contentValues,selection,args);

        }
        ContentValues contentValues = new ContentValues();
        contentValues.put("bookId",readingRecord.getBookId());
        contentValues.put("chapterId",readingRecord.getChapterId());;
        contentValues.put("characterIndex",readingRecord.getCharacterIndex());
        long res = sqlitedb.insert("readingRecords", null, contentValues);
        closeDataBase();
        return res;
    }



    //获取BookJson
    public ReadingRecord getReadingRecordOfBook(String bookId){
        getDatebase();
        String [] args={bookId};
        String selection="bookId=?";
        ReadingRecord readingRecord=null;
        Cursor cursor = sqlitedb.query("readingRecords", null, selection, args, null, null, null);
        if(cursor.getCount()>=1){
            cursor.moveToFirst();
            String bId=cursor.getString(cursor.getColumnIndex("bookId"));
            String chapterId=cursor.getString(cursor.getColumnIndex("chapterId"));
            long characterIndex=cursor.getLong(cursor.getColumnIndex("characterIndex"));
            readingRecord=new ReadingRecord(bId,chapterId,characterIndex);
        }
        closeDataBase();
        return readingRecord;
    }

}
