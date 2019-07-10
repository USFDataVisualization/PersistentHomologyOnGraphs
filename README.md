# PersistentHomologyOnGraphs
A visualization tool for using persistent homology to interact with undirected graphs.

------------------------
Publication:
Persistent Homology Guided Force-Directed Graph Layouts
Ashley Suh, Mustafa Hajij, Bei Wang, Carlos Scheidegger, Paul Rosen
InfoVis 2019

------------------------
Required Software
-------------------------
Processing 3.x - https://processing.org/download/

------------------------
How to run
------------------------
Our software is located in TopoFDL0D
Our datasets are located in TopoFDL0D/data
Our layouts generated for figures are located in TopoFDL0D/data/paper_layouts

Run Topo0DFDL in Processing and select one of the datasets provided:
    6ary.json 
    airport.json 
    barbell.json 
    bcsstk.json 
    caltech.json
    collab.science.json 
    lobster.json 
    map.science.json 
    miserables.json 
    smith.json
    train.json 
    us.senate.2007.antivote.json
    us.senate.2007.covote.json
    us.senate.2008.antivote.json
    us.senate.2008.covote.json


------------------------
Interaction
------------------------
All conventional FDL interactions are used (i.e., click and drag nodes etc.)
The persistence barcode on the left has three interactions:

1) The filter slider on the top can be moved to the right (to add contracting forces) or to the left (to remove contracting forces). Starting position has no contracting forces added.
2) Selecting a bar by left-clicking it will add repulsive forces. Select any bars to add a repulsive force, or unselect to remove the force.
3) For larger graphs, a hyperbolic zoom will appear. The zoom position can be adjusted by clicking and dragging or using a scroll wheel.


------------------------
ControlP5
------------------------
An interface in the bottom right of has several parameter options. All parameters have been pre-optimized for per layout, so they do not require any further fine tuning. 

Toggle the control window by pressing 'c'.

1) Standard Force Parameters: adjusts conventional forces
2) 0D PH Force Parameters: adjusts PH forces applied to bars that are contracted (spring constant) and repulsed (coulomb constant)
3) Drawing Parameters: adjusts sizes of nodes and edges
4) Colormap: coloring based on group IDs, specific to datasets
5) Reset Positions: resets the layout to a random layout
6) Save Data: (over)writes to original JSON file to save adjusted parameters

** Warning: If spring forces are turned up higher, you may need to proportionally lower the timestep to avoid issues within the force-directed layout **
