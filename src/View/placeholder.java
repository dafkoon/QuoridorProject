//if(wall.getOrientation() == Wall.Orientation.HORIZONTAL) { // Check Horizontal wall not intersecting others.
//        if (walls.contains(wall) ||
//        walls.contains(wall.neighbor(0, 0, Wall.Orientation.VERTICAL)) ||
//        walls.contains(wall.neighbor(0, -1, Wall.Orientation.HORIZONTAL)) ||
//        walls.contains(wall.neighbor(0, 1, Wall.Orientation.HORIZONTAL))) {
//        System.out.println(wall + " intersecting ");
//        return false;
//        }
//        System.out.println();
//        }
//        else { // Check Vertical wall not intersecting others.
//        if (walls.contains(wall) ||
//        walls.contains(wall.neighbor(0, 0, Wall.Orientation.HORIZONTAL)) ||
//        walls.contains(wall.neighbor(-1, 0, Wall.Orientation.VERTICAL)) ||
//        walls.contains(wall.neighbor(1, 0, Wall.Orientation.VERTICAL))) {
//        System.out.println(wall + " intersecting ");
//        return false;
//        }
//        System.out.println();
//        }