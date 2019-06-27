package subset;

import java.util.HashSet;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * @author <a href="https://github.com/ferenc4">Ferenc Fazekas</a>
 */
public class SubsetStream<N> {
    private final Stream<N> stream;
    private final int subsetSize;
    private final Set<VariableTracker> tracked;

    public SubsetStream(Stream<N> stream, int subsetSize) {
        this.stream = stream;
        this.subsetSize = subsetSize;
        this.tracked = new HashSet<>();
    }

    private SubsetStream(Stream<N> stream, int subsetSize, Set<VariableTracker> tracked) {
        this.stream = stream;
        this.subsetSize = subsetSize;
        this.tracked = tracked;
    }

    public SubsetStream<N> filter(Predicate<? super N> predicate) {
        return new SubsetStream<>(stream.filter(predicate), subsetSize);
    }

    public <V> SubsetStream<N> track(String variableName, V initValue, BiFunction<V, N, V> accumulator,
                                     Function<V, TrackedVariable> combiner,
                                     Class<V> runningClass, Class<N> newValClass) {
        tracked.add(new VariableTracker<>(variableName, initValue, accumulator, combiner, runningClass, newValClass));
        return this;
    }

    public <R> SubsetStream<R> map(Function<? super N, ? extends R> mapper) {
        return new SubsetStream<>(stream.map(mapper), subsetSize);
    }

    public SubSetStreamResult<N> collect() {
        SubSetStreamResult.Builder<N> builder = new SubSetStreamResult.Builder<>(subsetSize, tracked);
        stream.forEach(builder::add);
        return builder.build();
    }

    @Override
    public String toString() {
        return "SubsetStream{" +
                "stream=" + stream +
                ", subsetSize=" + subsetSize +
                ", tracked=" + tracked +
                '}';
    }
}