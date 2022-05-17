package me.devwckd.server.container_driver

interface ContainerDriver {
    fun createContainer(deploymentId: String, image: String): CreateContainerResult
    fun getContainerMetadata(id: String): ContainerMetadataResult?
    fun killContainer(id: String): KillContainerResult?

    fun purgeContainers(except: List<String> = listOf()): PurgeContainersResult
}

//interface CreateContainerDsl {
//    fun withName(name: String)
//    fun withEnvironmentVariables(vararg variables: Pair<String, String>)
//    fun withRandomAssignedPort(port: Int)
//}

interface CreateContainerResult {
    val containerId: String
    val publishedPort: Int
}

interface ContainerMetadataResult {
    val deploymentAddress: String
    val deploymentPort: Int
    val deploymentType: String
}

interface KillContainerResult {

}

interface PurgeContainersResult {
    val affected: Int
}