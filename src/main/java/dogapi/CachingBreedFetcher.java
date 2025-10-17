package dogapi;

import java.util.*;

/**
 * This BreedFetcher caches fetch request results to improve performance and
 * lessen the load on the underlying data source. An implementation of BreedFetcher
 * must be provided. The number of calls to the underlying fetcher are recorded.
 *
 * If a call to getSubBreeds produces a BreedNotFoundException, then it is NOT cached
 * in this implementation. The provided tests check for this behaviour.
 *
 * The cache maps the name of a breed to its list of sub breed names.
 */
public class CachingBreedFetcher implements BreedFetcher {
    // Completed implementation
    private int callsMade = 0;
    private final BreedFetcher delegate;
    private final Map<String, List<String>> cache = new HashMap<>();

    public CachingBreedFetcher(BreedFetcher fetcher) {
        this.delegate = Objects.requireNonNull(fetcher, "fetcher");
    }

    @Override
    public List<String> getSubBreeds(String breed) throws BreedNotFoundException {
        String key = (breed == null) ? null : breed.toLowerCase(Locale.ROOT);

        if (key != null && cache.containsKey(key)) {
            return new ArrayList<>(cache.get(key));
        }

        // record that we're calling the underlying fetcher
        callsMade++;

        try {
            List<String> result = delegate.getSubBreeds(breed);
            List<String> toStore = (result == null) ? Collections.emptyList() : new ArrayList<>(result);
            if (key != null) {
                cache.put(key, toStore);
            }
            return new ArrayList<>(toStore);
        } catch (BreedNotFoundException e) {
            // do not cache failures; propagate
            throw e;
        }
    }

    public int getCallsMade() {
        return callsMade;
    }
}