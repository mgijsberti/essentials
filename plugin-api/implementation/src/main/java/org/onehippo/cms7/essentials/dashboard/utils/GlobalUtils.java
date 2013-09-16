package org.onehippo.cms7.essentials.dashboard.utils;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.CharMatcher;
import com.google.common.base.Charsets;
import com.google.common.base.Strings;

/**
 * @version "$Id: GlobalUtils.java 174806 2013-08-23 09:22:46Z mmilicevic $"
 */
public class GlobalUtils {

    private static final String PREFIX_GET = "get";
    private static final Pattern NAMESPACE_PATTERN = Pattern.compile(":");
    private static Logger log = LoggerFactory.getLogger(GlobalUtils.class);

    private GlobalUtils() {
    }

    public static StringBuilder readStreamAsText(final InputStream stream) {
        final StringBuilder builder = new StringBuilder();
        try {
            final List<String> lines = IOUtils.readLines(stream);
            for (String line : lines) {
                builder.append(line);

            }
        } catch (IOException e) {
            log.error("Error reading files", e);
        } finally {
            IOUtils.closeQuietly(stream);
        }

        return builder;
    }

    public static void populateDirectories(final Path startPath, final List<Path> existing) {
        existing.add(startPath);
        try (final DirectoryStream<Path> stream = Files.newDirectoryStream(startPath)) {
            for (Path path : stream) {
                if (Files.isDirectory(path)) {
                    populateDirectories(path, existing);
                }
            }
        } catch (IOException e) {
            log.error("", e);
        }

    }

    /**
     * Read text file content (UTF-8)
     *
     * @param path path to read from
     * @return StringBuilder instance containing file content.
     */
    public static StringBuilder readTextFile(final Path path) {
        final StringBuilder builder = new StringBuilder();
        try {
            final List<String> strings = Files.readAllLines(path, Charsets.UTF_8);
            for (String string : strings) {
                builder.append(string).append(System.getProperty("line.separator"));
            }
        } catch (IOException e) {
            log.error("Error reading source file", e);
        }
        return builder;
    }

    /**
     * Write text content to a file (UTF-8)
     *
     * @param content text content
     * @param path    path to save file to
     */
    public static void writeToFile(final CharSequence content, final Path path) {
        try (BufferedWriter writer = Files.newBufferedWriter(path, Charsets.UTF_8)) {
            writer.append(content);
            writer.flush();
        } catch (IOException e) {
            log.error("Error writing file {}", e);
        }
    }

    /**
     * Creates method name for namespaced property e.g. {@code "myproject:myporperty"}
     *
     * @param name
     * @return
     */
    public static String createMethodName(String name) {
        if (Strings.isNullOrEmpty(name) || name.trim().equals(":")) {
            return EssentialConst.INVALID_METHOD_NAME;
        }
        name = CharMatcher.WHITESPACE.removeFrom(name);
        // replace all whitespaces:
        final int index = name.indexOf(':');
        if (index == -1 || index == name.length() - 1) {
            return PREFIX_GET + capitalize(name.replace(':', ' ').trim());
        }
        final String[] parts = NAMESPACE_PATTERN.split(name);
        if (parts.length < 1) {
            return EssentialConst.INVALID_METHOD_NAME;
        }
        return PREFIX_GET + capitalize(parts[1]);
    }

    private static String capitalize(final String name) {
        return name.substring(0, 1).toUpperCase() + name.substring(1);
    }

    public static String createClassName(final String name) {
        if (Strings.isNullOrEmpty(name) || name.trim().equals(":")) {
            return EssentialConst.INVALID_CLASS_NAME;
        }
        final int index = name.indexOf(':');
        if (index == -1 || index == name.length() - 1) {
            return capitalize(name.replace(':', ' ').trim());
        }
        final String[] parts = NAMESPACE_PATTERN.split(name);
        if (parts.length < 1) {
            return EssentialConst.INVALID_CLASS_NAME;
        }
        return capitalize(parts[1]);
    }
}