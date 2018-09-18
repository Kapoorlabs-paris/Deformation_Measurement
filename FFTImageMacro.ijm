makeRectangle(getWidth()/2,0, getWidth(), getHeight());
run("Crop");
T = 241;
DeltaT = 5;
Frequfactor = 1.0/(DeltaT * T);
run("Properties...", "channels=1 slices=1 frames=1 unit=minute pixel_width=Frequfactor pixel_height=Frequfactor voxel_depth=2.0000");