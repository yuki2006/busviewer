/**
 *
 */
package jp.co.yuki2006.busmap.map;

import android.content.SearchRecentSuggestionsProvider;

import jp.co.yuki2006.busmap.values.ProviderValues;

/**
 * @author yuki
 */
public class MapSuggestionProvider extends SearchRecentSuggestionsProvider {
    public MapSuggestionProvider() {

        setupSuggestions(ProviderValues.MAP_SUGGESTION_PROVIDER,
                MapSuggestionProvider.DATABASE_MODE_QUERIES);
    }
}
