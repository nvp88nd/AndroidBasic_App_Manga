package com.example.dbhelper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "BTL_Android.db"; // Tên file .db
    private static final int DATABASE_VERSION = 1; // Phiên bản cơ sở dữ liệu

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createTable(db);
        insertData(db);
    }

    private void createTable(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE manga (" +
                "mangaID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "mangaName TEXT, " +
                "author TEXT, " +
                "publish TEXT, " +
                "describle TEXT, " +
                "coverImage TEXT, " +
                "srcChapter TEXT, " +
                "chapterTotal INTEGER, " +
                "lastUpdate TEXT" +
                ");");
        db.execSQL("CREATE TABLE account (" +
                "email TEXT PRIMARY KEY, " +
                "password TEXT, " +
                "userName TEXT" +
                ");");
        db.execSQL("CREATE TABLE genres (" +
                "genresID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "genresName TEXT UNIQUE" +
                ");");
        db.execSQL("CREATE TABLE favorites (" +
                "email TEXT, " +
                "mangaID INTEGER, " +
                "PRIMARY KEY(email, mangaID), " +
                "FOREIGN KEY(mangaID) REFERENCES manga(mangaID)" +
                ");");
        db.execSQL("CREATE TABLE history (" +
                "email TEXT, " +
                "mangaID INTEGER, " +
                "lastReadChapter INTEGER, " +
                "lastReadTime TEXT, " +
                "PRIMARY KEY(email, mangaID), " +
                "FOREIGN KEY(email) REFERENCES account(email)," +
                "FOREIGN KEY(mangaID) REFERENCES manga(mangaID)" +
                ");");
        db.execSQL("CREATE TABLE manga_genres (" +
                "genresID INTEGER, " +
                "mangaID INTEGER, " +
                "PRIMARY KEY(genresID, mangaID), " +
                "FOREIGN KEY(genresID) REFERENCES genres(genresID), " +
                "FOREIGN KEY(mangaID) REFERENCES manga(mangaID)" +
                ");");
    }

    private void insertData(SQLiteDatabase db) {
        db.execSQL("INSERT INTO manga (mangaName, author, publish, describle, coverImage, srcChapter, chapterTotal, lastUpdate) " +
                "VALUES ('Võ Luyện Đỉnh Phong', 'Tên Tác Giả', 'Nhà Xuất Bản', 'Võ đạo đỉnh phong, là cô độc, là tịch mịch, là dài đằng đẵng cầu tác, là cao xử bất thắng hàn\n" +
                "Phát triển trong nghịch cảnh, cầu sinh nơi tuyệt địa, bất khuất không buông tha, mới có thể có thể phá võ chi cực đạo.\n" +
                "Lăng Tiêu các thí luyện đệ tử kiêm quét rác gã sai vặt Dương Khai ngẫu lấy được một bản vô tự hắc thư, từ nay về sau đạp vào dài đằng đẵng võ đạo.'," +
                "'/Pictures/Truyen/vo-luyen-dinh-phong/vo-luyen-dinh-phong.jpg'," +
                "'/Pictures/Truyen/vo-luyen-dinh-phong/', 10, '2024-11-21'), " +
                "('Đại Quản Gia Là Ma Hoàng', 'Tên Tác Giả', 'Nhà Xuất Bản', 'Mô Tả Manga'," +
                "'/Pictures/Truyen/dai-quan-gia-la-ma-hoang/dai-quan-gia-la-ma-hoang.jpg'," +
                "'/Pictures/Truyen/dai-quan-gia-la-ma-hoang/', 10, '2024-11-21')," +
                "('Nguyên Tôn', 'Tên Tác Giả', 'Nhà Xuất Bản', 'Mô Tả Manga'," +
                "'/Pictures/Truyen/nguyen-ton/nguyen-ton.jpg'," +
                "'/Pictures/Truyen/nguyen-ton/', 10, '2024-11-21')," +
                "('Đại Tượng Vô Hình', 'Tên Tác Giả', 'Nhà Xuất Bản', 'Mô Tả Manga'," +
                "'/Pictures/Truyen/dai-tuong-vo-hinh/dai-tuong-vo-hinh.jpg'," +
                "'/Pictures/Truyen/dai-tuong-vo-hinh/', 10, '2024-11-21')," +
                "('Bách Luyện Thành Thần', 'Tên Tác Giả', 'Nhà Xuất Bản', 'Mô Tả Manga'," +
                "'/Pictures/Truyen/bach-luyen-thanh-than/bach-luyen-thanh-than.jpg'," +
                "'/Pictures/Truyen/bach-luyen-thanh-than/', 10, '2024-11-21')," +
                "('Khánh Dư Niên', 'Tên Tác Giả', 'Nhà Xuất Bản', 'Mô Tả Manga'," +
                "'/Pictures/Truyen/khanh-du-nien/khanh-du-nien.jpg'," +
                "'/Pictures/Truyen/khanh-du-nien/', 10, '2024-11-21')," +
                "('Vạn Cổ Chí Tôn', 'Tên Tác Giả', 'Nhà Xuất Bản', 'Mô Tả Manga'," +
                "'/Pictures/Truyen/van-co-chi-ton/van-co-chi-ton.jpg'," +
                "'/Pictures/Truyen/van-co-chi-ton/', 10, '2024-11-21')," +
                "('Vạn Cổ Tối Cường Tông', 'Tên Tác Giả', 'Nhà Xuất Bản', 'Mô Tả Manga'," +
                "'/Pictures/Truyen/van-co-toi-cuong-tong/van-co-toi-cuong-tong.jpg'," +
                "'/Pictures/Truyen/van-co-toi-cuong-tong/', 10, '2024-11-21')," +
                "('Toàn Trí Độc Giả', 'Tên Tác Giả', 'Nhà Xuất Bản', 'Mô Tả Manga'," +
                "'/Pictures/Truyen/toan-tri-doc-gia/toan-tri-doc-gia.jpg'," +
                "'/Pictures/Truyen/toan-tri-doc-gia/', 10, '2024-11-21')," +
                "('Thảm Họa Tử Linh Sư', 'Tên Tác Giả', 'Nhà Xuất Bản', 'Mô Tả Manga'," +
                "'/Pictures/Truyen/tham-hoa-tu-linh-su/tham-hoa-tu-linh-su.jpg'," +
                "'/Pictures/Truyen/tham-hoa-tu-linh-su/', 10, '2024-11-21')," +
                "('Onepunch Man', 'Tên Tác Giả', 'Nhà Xuất Bản', 'Mô Tả Manga'," +
                "'/Pictures/Truyen/onepunch-man/onepunch-man.jpg'," +
                "'/Pictures/Truyen/onepunch-man/', 10, '2024-11-21')," +
                "('Bậc Thầy Thiết Kế Điền Trang', 'Tên Tác Giả', 'Nhà Xuất Bản', 'Mô Tả Manga'," +
                "'/Pictures/Truyen/bac-thay-thiet-ke-dien-trang/bac-thay-thiet-ke-dien-trang.jpg'," +
                "'/Pictures/Truyen/bac-thay-thiet-ke-dien-trang/', 10, '2024-11-21')," +
                "('Ta Là Tà Đế', 'Tên Tác Giả', 'Nhà Xuất Bản', 'Mô Tả Manga'," +
                "'/Pictures/Truyen/ta-la-ta-de/ta-la-ta-de.jpg'," +
                "'/Pictures/Truyen/ta-la-ta-de/', 10, '2024-11-21')," +
                "('Chưởng Môn Khiêm Tốn Chút', 'Tên Tác Giả', 'Nhà Xuất Bản', 'Mô Tả Manga'," +
                "'/Pictures/Truyen/chuong-mon-khiem-ton-chut/chuong-mon-khiem-ton-chut.jpg'," +
                "'/Pictures/Truyen/chuong-mon-khiem-ton-chut/', 10, '2024-11-21')," +
                "('Gia Đình Điệp Viên', 'Tên Tác Giả', 'Nhà Xuất Bản', 'Mô Tả Manga'," +
                "'/Pictures/Truyen/gia-dinh-diep-vien/gia-dinh-diep-vien.jpg'," +
                "'/Pictures/Truyen/gia-dinh-diep-vien/', 10, '2024-11-21')," +
                "('Gacha Vô Hạn', 'Tên Tác Giả', 'Nhà Xuất Bản', 'Mô Tả Manga'," +
                "'/Pictures/Truyen/gacha-vo-han/gacha-vo-han.jpg'," +
                "'/Pictures/Truyen/gacha-vo-han/', 10, '2024-11-21')," +
                "('One Piece', 'Tên Tác Giả', 'Nhà Xuất Bản', 'Mô Tả Manga'," +
                "'/Pictures/Truyen/one-piece/one-piece.jpg'," +
                "'/Pictures/Truyen/one-piece/', 10, '2024-11-21')," +
                "('Tiên Võ Đế Tôn', 'Tên Tác Giả', 'Nhà Xuất Bản', 'Mô Tả Manga'," +
                "'/Pictures/Truyen/tien-vo-de-ton/tien-vo-de-ton.jpg'," +
                "'/Pictures/Truyen/tien-vo-de-ton/', 10, '2024-11-21')," +
                "('Lạn Kha Kỳ Duyên', 'Tên Tác Giả', 'Nhà Xuất Bản', 'Mô Tả Manga'," +
                "'/Pictures/Truyen/lan-kha-ky-duyen/lan-kha-ky-duyen.jpg'," +
                "'/Pictures/Truyen/lan-kha-ky-duyen/', 10, '2024-11-21')," +
                "('Ta Có Một Sơn Trại', 'Tên Tác Giả', 'Nhà Xuất Bản', 'Mô Tả Manga'," +
                "'/Pictures/Truyen/ta-co-mot-son-trai/ta-co-mot-son-trai.jpg'," +
                "'/Pictures/Truyen/ta-co-mot-son-trai/', 10, '2024-11-21');");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Xử lý nâng cấp database
    }
}
