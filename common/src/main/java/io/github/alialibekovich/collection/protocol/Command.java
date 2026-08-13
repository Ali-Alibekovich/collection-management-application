package io.github.alialibekovich.collection.protocol;

import java.io.Serializable;

/**
 * Marker interface for every command that travels between the client and the server.
 *
 * <p>The client and the server each provide their own implementation of a command
 * with the same fully qualified name and {@code serialVersionUID}: the client-side
 * variant knows how to build a request, while the server-side variant knows how to
 * execute it. Java serialization resolves the class by name on deserialization,
 * which is what makes this "twin class" exchange work.</p>
 */
public interface Command extends Serializable {
}
