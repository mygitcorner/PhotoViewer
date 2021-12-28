package gf.photoviewer.event;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class PVHandler<T> {
	List<Consumer<T>> handlers;
	
	public PVHandler() {
		handlers = new ArrayList<>();
	}
	
	public void addListener(Consumer<T> r) {
		handlers.add(r);
	}
	
	public void invoke(T event) {
		handlers.forEach(h -> h.accept(event));
	}
}
