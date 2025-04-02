package ac.grim.grimac.utils.math;

// Optifine fastmath is terrible.
//
// Optifine fastmath sends NaN while using an elytra
// It allows jumps that aren't possible in vanilla
// It changes movement by 0.0001
//
// Link to issue:
// https://github.com/sp614x/optifine/issues/5578
//
// Response by sp614x - Optifine's author:
// "If the anticheat fails due to a position difference of 1e-4m (1mm), then it has some problems.
// It should have a tolerance for player actions that is well above 1mm, probably 10cm or something."
//
// No, if your client is flagging my anticheat for not following vanilla behavior, that is on you!
// My anticheat flagging 1e-4 means it's very good, not that it has issues.
//
// I'd suggest everyone to go use Sodium instead as it's open source, is usually faster, and follows vanilla behavior
//
// I don't care when vanilla does something stupid, but I get angry when a proprietary mod breaks my anticheat
//

// Update a few months later

// WHY DID THEY CHANGE FASTMATH
// This is impossible, and I give up!
//
// Instead of fixing the damn issue of changing vanilla mechanics, the new version patches some
// issues with half angles.  Yes, it was wrong, so they made it more accurate, but this makes our
// job impossible without significant performance degradation and 1e-4 bypasses from switching
// between whichever trig table gives the most advantage.
//
// YOU ARE NOT VANILLA OPTIFINE.  YOU DO NOT CONTROL WHAT VANILLA MOVEMENT IS!
//
// I'm seriously considering allowing a warning for FastMath users that it may lead to false bans
// his arrogance is impossible to patch.
//
public class OptifineFastMath {
    // Mathematical Concept: Lookup Table (LUT) for sine values
    // This is a pre-calculated table of 4096 sine values to avoid expensive trigonometric calculations
    private static final float[] SIN_TABLE_FAST = new float[4096];
    
    // Mathematical Concept: Conversion constant from radians to lookup table index
    // 651.8986469044033 = 4096 / (2*PI) - converts angle in radians to table index
    private static final float radToIndex = roundToFloat(651.8986469044033d);

    static {
        // Mathematical Concept: Precomputation of sine function values
        // Calculate sine values for 4096 equally spaced points around the unit circle
        for (int j = 0; j < SIN_TABLE_FAST.length; ++j) {
            SIN_TABLE_FAST[j] = roundToFloat(StrictMath.sin((double) j * Math.PI * 2d / 4096d));
        }
    }

    // Mathematical Concept: Fast sine approximation using table lookup
    // Instead of calculating sin(x) directly, lookup precomputed value for speed
    public static float sin(float value) {
        return SIN_TABLE_FAST[(int) (value * radToIndex) & 4095];
    }

    // Mathematical Concept: Fast cosine approximation using table lookup
    // Uses identity cos(x) = sin(x + π/2) and table lookup for speed
    // 1024 = 4096/4, representing π/2 in table indices
    public static float cos(float value) {
        return SIN_TABLE_FAST[(int) (value * radToIndex + 1024f) & 4095];
    }

    // Mathematical Concept: Precision control through rounding
    // Limits precision to reduce floating point errors while maintaining reasonable accuracy
    public static float roundToFloat(double d) {
        return (float) ((double) Math.round(d * 1.0E8d) / 1.0E8d);
    }
}
