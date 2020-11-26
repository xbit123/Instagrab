package com.ruxbit.instagrab

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Entity
data class User(
    @PrimaryKey val login: String,
    @ColumnInfo(name = "name_surname") val nameSurname: String
)

@Dao
interface UserDao {
    @Query("SELECT * FROM User")
    fun getAll(): Flow<List<User>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(user: User)
}

@Database(entities = [User::class], version = 1)
abstract class AppDatabase: RoomDatabase() {
    abstract fun userDao(): UserDao

    companion object {
        @Volatile
        private var instance: AppDatabase? = null

        fun getInstance(): AppDatabase = instance ?: synchronized(this) {
            instance ?: buildDatabase().also { instance = it }
        }

        private fun buildDatabase(): AppDatabase =
            Room.databaseBuilder(App.appContext, AppDatabase::class.java, "db").build()
    }
}