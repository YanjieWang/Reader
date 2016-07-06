package reader.a6pu.com.reader.entity;

/**
 * Created by 王燕杰 on 2016/5/29.
 */
public class BookType {
    private String typeName;
    private String typeId;

    public BookType() {
    }
    public BookType(String typeName, String typeId) {
        this.typeName = typeName;
        this.typeId = typeId;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getTypeId() {
        return typeId;
    }

    public void setTypeId(String typeId) {
        this.typeId = typeId;
    }

    @Override
    public String toString() {
        return "Novel{" +
                "typeName='" + typeName + '\'' +
                ", typeId='" + typeId + '\'' +
                '}';
    }
}
