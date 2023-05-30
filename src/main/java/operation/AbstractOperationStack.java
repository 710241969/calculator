package operation;

public abstract class AbstractOperationStack<T> {

    protected abstract T undo();

    protected abstract T redo();

    protected abstract void recordResult(T result);

}
