package com.example.onlineshoproom

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase


@Database(
    entities = [
        UserEntity::class,
        ProductEntity::class,
        OrderEntity::class,
        CategoryEntity::class,
        ReviewEntity::class,
        ProductCategoryCrossRef::class,
        OrderProductCrossRef::class
    ],
    version = 1,
    exportSchema = false
)
abstract class ShopDatabase : RoomDatabase() {
    abstract fun shopDao(): ShopDao

    companion object {
        private var database: ShopDatabase? = null

        fun getDatabase(context: Context): ShopDatabase {
            if (database == null) {
                database = Room.databaseBuilder(
                    context.applicationContext,
                    ShopDatabase::class.java,
                    "online_shop_room.db"
                )
                    .allowMainThreadQueries()
                    .build()
            }

            return database!!
        }
    }
}
