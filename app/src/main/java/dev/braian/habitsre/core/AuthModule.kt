package dev.braian.habitsre.core

import android.app.Application
import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.braian.habitsre.BuildConfig
import dev.braian.habitsre.R
import dev.braian.habitsre.domain.repository.AuthRepository
import dev.braian.habitsre.domain.repository.AuthRepositoryImpl
import dev.braian.habitsre.domain.repository.HabitRepository
import dev.braian.habitsre.domain.repository.HabitRepositoryImpl
import javax.inject.Named
import javax.inject.Singleton

const val WEB_CLIENT_ID = "873538241717-8garl8oef5df5lpr2klrgrl3emln1cf1.apps.googleusercontent.com"

@Module
@InstallIn(SingletonComponent::class)
class AuthModule {

    @Provides
    @Singleton
    fun providerFirebaseAuth() = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun providesRealtimeDBReference() : DatabaseReference = Firebase.database.getReference("habit")

    @Provides
    fun provideHabitRepository(impl: HabitRepositoryImpl): HabitRepository = impl

    @Provides
    fun provideAuthRepository(impl: AuthRepositoryImpl): AuthRepository = impl

    @Provides
    fun provideOneTapClient(
        @ApplicationContext
        context: Context
    ) = Identity.getSignInClient(context)

    @Provides
    fun provideCredentialManager(
        @ApplicationContext context: Context
    ) = CredentialManager.create(context)


    @Provides
    @Named(Constants.TAG.GOOGLE_SIGN_IN_REQUEST)
    fun provideGoogleSignInRequest(
        app: Application
    ) = GetCredentialRequest.Builder()
        .addCredentialOption(
            GetGoogleIdOption.Builder()
                // Only show accounts previously used to sign in.
                .setFilterByAuthorizedAccounts(true)
                // Your server's client ID, not your Android client ID.
                .setServerClientId(BuildConfig.WEB_CLIENT_ID)
                // Automatically sign in when exactly one credential is retrieved.
                .setAutoSelectEnabled(true)
                .build()
        )
        .build()

    @Provides
    @Named(Constants.TAG.GOOGLE_SIGN_UP_REQUEST)
    fun provideGoogleSignUpRequest(
        app: Application
    ) = GetCredentialRequest.Builder()
        .addCredentialOption(
            GetGoogleIdOption.Builder()
                // Show all accounts on the device.
                .setFilterByAuthorizedAccounts(false)
                // Your server's client ID, not your Android client ID.
                .setServerClientId(BuildConfig.WEB_CLIENT_ID)
                .build()
        )
        .build()

}