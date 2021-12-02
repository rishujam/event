package ind.ev.events.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ind.ev.events.db.OtpApi
import ind.ev.events.db.ProfileDao
import ind.ev.events.db.ProfileDatabase
import ind.ev.events.firebase.FirebaseSource
import ind.ev.events.models.Profile
import ind.ev.events.repository.Repository
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

private const val BASE_URL = "https://2factor.in/API/V1/"

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Singleton
    @Provides
    fun providesFirebase() = FirebaseSource()


    @Singleton
    @Provides
    fun provideChannelDao(database: ProfileDatabase) = database.getProfileDao()


    @Singleton
    @Provides
    fun providesDatabase(@ApplicationContext context: Context):ProfileDatabase = Room.databaseBuilder(
            context,
            ProfileDatabase::class.java,
            "profileReader"
        ).build()

    @Singleton
    @Provides
    fun provideOtpApi():OtpApi = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(OtpApi::class.java)
}