/**
 *
 */
package jp.co.yuki2006.busmap;

import android.content.SearchRecentSuggestionsProvider;

import jp.co.yuki2006.busmap.values.ProviderValues;

/**
 * @author yuki
 */
public class BusStopSuggestionProvider extends SearchRecentSuggestionsProvider {

    public BusStopSuggestionProvider() {
        setupSuggestions(ProviderValues.BUS_STOP_SUGGESTION_PROVIDER,
                BusStopSuggestionProvider.DATABASE_MODE_QUERIES);

    }
}
