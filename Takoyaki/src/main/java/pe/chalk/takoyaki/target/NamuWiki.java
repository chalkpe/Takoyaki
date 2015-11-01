package pe.chalk.takoyaki.target;

import org.json.JSONArray;
import org.json.JSONObject;
import pe.chalk.takoyaki.Staff;
import pe.chalk.takoyaki.filter.namu.wiki.StatusFilter;

/**
 * @author ChalkPE <chalkpe@gmail.com>
 * @since 2015-11-01
 */
public class NamuWiki extends Target<JSONArray> {
    public NamuWiki(JSONObject properties){
        super(properties.getString("prefix"), properties.getLong("interval"));
        this.getFilters().add(new StatusFilter(this));

        this.setName(this.getName() + " (나무위키)");
        this.staff = new Staff(this.getLogger(), properties.getInt("timeout"), "UTF-8");
    }

    @Override
    public JSONArray getDocument() throws Exception{
        return new JSONArray(this.getStaff().parse("https://namu.wiki/sidebar.json"));
    }
}
