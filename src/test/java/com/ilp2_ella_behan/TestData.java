import com.ilp2_ella_behan.data.Position;
import com.ilp2_ella_behan.data.RestrictedArea;

import java.util.List;

public final class TestData {
    private TestData(){}

    public static RestrictedArea squareRestrictedArea(double lat, double lng, double half) {
        RestrictedArea ra = new RestrictedArea(
                ra.setVertices(List.of(
                        new Position(lat - half, lng - half),
                        new Position(lat - half, lng + half),
                        new Position(lat + half, lng + half),
                        new Position(lat + half, lng - half),
                        new Position(lat - half, lng - half)
                ));
        return ra;
        )
    }
}