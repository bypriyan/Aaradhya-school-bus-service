package com.bypriyan.togocartstore.DI.module

import android.content.Context
import android.content.SharedPreferences
import android.location.Geocoder
import com.bypriyan.aaradhyaschoolbusservice.api.ApiServiceLogin
import com.bypriyan.aaradhyaschoolbusservice.api.ApiServiceOTP
import com.bypriyan.aaradhyaschoolbusservice.api.ApiServiceRegisterUser
import com.bypriyan.aaradhyaschoolbusservice.api.ApiServiceUserDetails
import com.bypriyan.bustrackingsystem.utility.PreferenceManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.Calendar
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://aaradhyaschoolbusservice.com/api/v1/") // Replace with your base URL
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideApiServiceOTP(retrofit: Retrofit): ApiServiceOTP {
        return retrofit.create(ApiServiceOTP::class.java)
    }

    @Provides
    @Singleton
    fun provideApiServiceRegisterUser(retrofit: Retrofit): ApiServiceRegisterUser {
        return retrofit.create(ApiServiceRegisterUser::class.java)
    }

    @Provides
    @Singleton
    fun provideApiServiceLogin(retrofit: Retrofit): ApiServiceLogin {
        return retrofit.create(ApiServiceLogin::class.java)
    }

    @Provides
    @Singleton
    fun provideApiServiceUserDetails(retrofit: Retrofit): ApiServiceUserDetails {
        return retrofit.create(ApiServiceUserDetails::class.java)
    }

    @Provides
    @Singleton
    fun providePreferenceManager(@ApplicationContext context: Context): PreferenceManager {
        return PreferenceManager(context)
    }
}