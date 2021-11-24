package uz.star.mardexworker.di.modules

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import uz.star.mardexworker.ui.dialogs.DialogMaker
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DialogModule {

    @Provides
    @Singleton
    fun getDialogMaker(): DialogMaker = DialogMaker
}