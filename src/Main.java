import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.SequenceNode;

import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;

public class Main
{
    private final static String YAML_PATH = "C:\\projects\\EnglishCells\\yaml\\words.yml";
    private final static String TEXT_PATH = "C:\\projects\\EnglishCells\\text\\sentences.txt";
    private final static String CELLS_PATH = "C:\\projects\\EnglishCells\\text\\cells.txt";
    private final static String UNKNOWN_PATH = "C:\\projects\\EnglishCells\\text\\unknown.txt";

    public static void main(String[] args) throws FileNotFoundException
    {
        ArrayList<String> unknownCells = new ArrayList<>();
        HashMap<String, ArrayList<String>> cells = new HashMap<>();
        Yaml yaml = new Yaml(new ListConstructor<>(Word.class));
        try(InputStream inputStream = new FileInputStream(new File(YAML_PATH)))
        {
            ArrayList<Word> ws = yaml.load(inputStream);
            try(BufferedReader br = new BufferedReader (new FileReader(TEXT_PATH, Charset.forName("windows-1251"))))
            {
                String line;
                while ((line = br.readLine()) != null)
                {
                    if (line.trim().isEmpty())
                        continue;
                    if (line.startsWith("//"))
                        continue;
                    boolean found = false;
                    for (Word w : ws)
                    {
                        if (w == null || w.getKey() == null)
                            continue;
                        String l = line;
                        int index = line.indexOf('[');
                        if (index >= 0)
                            l = line.substring(0, index).trim();
                        if (l.contains(w.getKey()))
                        {
                            ArrayList<String> list = cells.computeIfAbsent(w.getKey(), k -> new ArrayList<>());
                            list.add(line);
                            found = true;
                        }
                    }
                    if (!found)
                        unknownCells.add(line);
                }
            }
            catch(IOException ex)
            {
                System.out.println(ex.getMessage());
            }
        }
        catch (IOException ex)
        {
            System.out.println(ex.getMessage());
        }
        // Output the cells.
        if (!cells.isEmpty())
        {
            try (OutputStream out = new FileOutputStream(new File(CELLS_PATH)))
            {
                for (String key : cells.keySet())
                {
                    ArrayList<String> list = cells.get(key);
                    if (list == null || list.isEmpty())
                        continue;
                    key = "------------- " + key + " -------------" + System.lineSeparator();
                    out.write(key.getBytes());
                    for (String v : list)
                    {
                        v += System.lineSeparator();
                        out.write(v.getBytes());
                    }
                    key = System.lineSeparator();
                    out.write(key.getBytes());
                }
            } catch (IOException ex)
            {
                System.out.println(ex.getMessage());
            }
        }
        if (unknownCells.isEmpty())
            return;
        // Output the unknown cells.
        try(OutputStream out = new FileOutputStream(new File(UNKNOWN_PATH)))
        {
            for (String c : unknownCells)
            {
                c += System.lineSeparator();
                out.write(c.getBytes());
            }
        }
        catch (IOException ex)
        {
            System.out.println(ex.getMessage());
        }
    }

    public static class ListConstructor<T> extends Constructor
    {
        private final Class<T> clazz;

        public ListConstructor(final Class<T> clazz)
        {
            this.clazz = clazz;
        }

        @Override
        protected Object constructObject(final Node node) {
            if (node instanceof SequenceNode && isRootNode(node)) {
                ((SequenceNode) node).setListType(clazz);
            }
            return super.constructObject(node);
        }

        private boolean isRootNode(final Node node) {
            return node.getStartMark().getIndex() == 0;
        }
    }
}
