Mechanical Power is a simple system. There are three types of blocks:
* MechanicalSource ("sources" from now on)
* MechanicalSink ("sinks" from now on)
* MechanicalConnectors ("connectors" from now on)

These are all interfaces. There is also AbstractMechanicalJunction ("junctions" from now on), which extends Block and is both a source, sink and connector. The primary example of this is the Gearbox.

Sinks and sources always have BlockEntities associated with them, while most of the data on the networks, particularly on the connectors, is stored in the sinks (remember, these also include junctions). Sinks also drive much of the updates in the network.

Mechanical Power is similar to redstone in that its connectors are not BlockEntities and fully driven by neighbor state updates.