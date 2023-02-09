package S;
import java.util.ArrayList;
import java.io.PrintStream;
import java.io.File;
import java.io.FileNotFoundException;

class Journal
{
    private ArrayList<String> entries = new ArrayList<>();
    private int count = 0;

    public void addEntry(String text)
    {
        count++;
        entries.add(count + ": " + text);
    }

    public void removeEntry(int index)
    {
        count++;
        entries.remove(index);
    }

    @Override
    public String toString()
    {
        return String.join(System.lineSeparator(), entries);
    }
}

class Persistence
{
	public void saveToFile (Journal journal,
							String filename,
							boolean overwrite) throws FileNotFoundException
	{
		if (overwrite || new File(filename).exists())
		{
			try (PrintStream out = new PrintStream(filename))
			{
				out.println(journal.toString());
			}
		}
	}
}

class SOLID_S
{
    public static void main(String[] args) throws Exception {
        Journal j = new Journal();
        j.addEntry("Hello World");
        j.addEntry("The world is fire");
        System.out.println(j);

        Persistence p = new Persistence();
        String filename = "journal.txt";
        p.saveToFile(j, filename, true);

        Runtime.getRuntime().exec("notepad.exe " + filename);
    }
}
