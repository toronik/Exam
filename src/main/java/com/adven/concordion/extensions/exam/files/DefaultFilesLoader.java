package com.adven.concordion.extensions.exam.files;

import com.adven.concordion.extensions.exam.html.Html;
import com.google.common.io.Files;
import nu.xom.Document;
import nu.xom.Builder;
import nu.xom.ParsingException;
import org.concordion.api.Evaluator;

import java.io.IOException;

import java.io.File;
import java.nio.charset.Charset;

import static com.adven.concordion.extensions.exam.PlaceholdersResolver.resolveXml;
import static com.google.common.base.Strings.isNullOrEmpty;
import static com.google.common.io.Resources.getResource;
import static java.io.File.separator;
import static java.lang.Boolean.parseBoolean;

public class DefaultFilesLoader implements FilesLoader {

    private static final Charset CHARSET = Charset.forName("UTF-8");

    @Override
    public void clearFolder(String path) {

        final File dir = new File(path);

        File[] files = dir.listFiles();

        if (files != null) {
            for (File file : files) {
                if (!file.delete()) {
                    throw new RuntimeException("could not delete file " + file.getPath());
                }
            }
        }
    }

    @Override
    public void createFileWith(String filePath, String fileContent) {

        try {

            File to = new File(filePath);

            if (to.createNewFile() && !isNullOrEmpty(fileContent)) {

                Files.append(fileContent, to, CHARSET);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String[] getFileNames(String path) {

        final File dir = new File(path);

        String[] names = dir.list();

        return names;
    }

    @Override
    public boolean fileExists(String filePath) {

        File actual = new File(filePath);

        return actual.exists();
    }

    public Document documentFrom(String path) {
        try {

            File xml = new File(path);

            return new Builder().build(xml);
        } catch (ParsingException | IOException e) {
            throw new RuntimeException("invalid xml", e);
        }
    }


    public String readFile(String path, String file) {

        String readRes = "";

        try {

            File fileToRead = new File(path + separator + file);

            readRes = Files.toString(fileToRead, CHARSET);
        } catch (IOException e) {
            readRes = "ERROR WHILE FILE READING";
        }

        return readRes;
    }

    public FileTag readFileTag(Html f, Evaluator eval) {
        final String content = getContentFor(f);
        return FileTag.builder().
                name(f.attr("name")).
                content(content == null ? null : resolveXml(content, eval).trim()).
                autoFormat(parseBoolean(f.attr("autoFormat"))).
                lineNumbers(parseBoolean(f.attr("lineNumbers"))).build();
    }

    private String getContentFor(Html f) {
        final String from = f.attr("from");
        return from == null
                ? f.hasChildren() ? f.text() : null
                : readFile(new File(getResource(from).getFile()));
    }

    protected String readFile(File file) {
        try {
            return Files.toString(file, CHARSET);
        } catch (IOException e) {
            return "ERROR WHILE FILE READING";
        }
    }
}
