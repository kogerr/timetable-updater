package org.zenbot.timetableupdater.configuration

import org.apache.commons.io.FileUtils
import org.slf4j.LoggerFactory
import org.springframework.core.env.Environment
import org.springframework.core.io.Resource
import org.springframework.core.io.UrlResource
import java.io.File
import java.io.IOException


open class ResourceReader(private val environment: Environment, private val properties: TimetableResourceLocationProperties) {

    val log = LoggerFactory.getLogger(this::class.java)

    @Throws(IOException::class)
    fun readResources(): List<Resource> {
        val resourceDirectory = File(this.javaClass.getResource(File.separator + properties.folder).file)
        if (!resourceDirectory.isDirectory) {
            throw IllegalStateException("Must be a directory")
        }

        log.info("Reading resources from [{}]", resourceDirectory.absolutePath)

        val activeProfiles = environment.activeProfiles
        log.info("Active profiles [{}]", activeProfiles.joinToString(","))
        return read(resourceDirectory, activeProfiles)
    }

    @Throws(IOException::class)
    private fun read(resourceDirectory: File, profiles: Array<String>): List<Resource> {
        val resources = ArrayList<Resource>()
        val fileResources = getFileResourcesByActiveProfiles(profiles, resourceDirectory)
        for (file in fileResources) {
            val urlResources= FileUtils.readFileToString(file, "UTF-8")
                    .split(System.lineSeparator())
                    .filter { line -> !line.isEmpty() && !line.startsWith(properties.commentSign) }
                    .map { line -> UrlResource(line) }
            resources.addAll(urlResources)
        }

        return resources
    }

    private fun getFileResourcesByActiveProfiles(activeProfiles: Array<String>, resourceDirectory: File): List<File> {
        val activeProfileFilesNames = activeProfiles.map { activeProfile -> activeProfile.plus(".${properties.fileExtension}") }
        val files = resourceDirectory.listFiles()
        return if (activeProfiles.size > 0) {
            files.filter { file -> activeProfileFilesNames.contains(file.name) }.toList()
        } else {
            files.asList()
        }

    }
}