@Module
@InstallIn(SingletonComponent::class)
object RemoteModule {

    @Provides
    @Singleton
    fun provideRemoteDatabase(
        @ApplicationContext context: Context
    ): RemoteDatabase =
        Room.databaseBuilder(
            context,
            RemoteDatabase::class.java,
            "aiope_remote.db"
        )
            .addMigrations(MIGRATION_1_2)
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    fun provideRemoteServerDao(db: RemoteDatabase): RemoteServerDao =
        db.remoteServerDao()
}

@Module
@InstallIn(SingletonComponent::class)
abstract class RemoteBindsModule {

    @Binds
    @Singleton
    abstract fun bindSshSessionManager(
        impl: SshSessionManager
    ): SshSessionManager

    @Binds
    @Singleton
    abstract fun bindGateway(
        impl: DefaultRemoteExecutionGateway
    ): RemoteExecutionGateway

    @Binds
    @Singleton
    abstract fun bindRouter(
        impl: RemoteExecutionRouter
    ): RemoteExecutionRouter

    @Binds
    @Singleton
    abstract fun bindCapabilityRegistry(
        impl: RemoteCapabilityRegistry
    ): RemoteCapabilityRegistry

    @Binds
    @Singleton
    abstract fun bindToolFilter(
        impl: ToolCapabilityFilter
    ): ToolCapabilityFilter

    @Binds
    @Singleton
    abstract fun bindToolBridge(
        impl: RemoteToolProvider
    ): com.aiope2.core.model.RemoteToolBridge
}