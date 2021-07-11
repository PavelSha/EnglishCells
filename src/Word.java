public class Word
{
    private int id;
    private String key;
    private String categories;
    private int current_match_count;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getKey() { return key; }
    public void setKey(String key) { this.key = key; }
    public String getCategories() { return categories; }
    public void setCategories(String categories) { this.categories = categories; }
    public int getcurrent_match_count() { return current_match_count; }
    public void setcurrent_match_count(int v) { this.current_match_count = v; }
}
