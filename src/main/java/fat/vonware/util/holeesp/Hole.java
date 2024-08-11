package fat.vonware.util.holeesp;

import fat.vonware.util.holeesp.Enums.SafetyEnum;
import net.minecraft.util.math.BlockPos;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class Hole {

    private BlockPos first;
    private BlockPos second;
    private final SafetyEnum safety;

    public Hole(final BlockPos first, final SafetyEnum safety) {
        this.first = first;
        this.safety = safety;
    }

    public Hole(final BlockPos first, final BlockPos second, final SafetyEnum safety) {
        this.first = first;
        this.second = second;
        this.safety = safety;
    }

    public void setFirst(final BlockPos first) {
        this.first = first;
    }

    public BlockPos getFirst() {
        return this.first;
    }

    public void setSecond(final BlockPos second) {
        this.second = second;
    }

    public BlockPos getSecond() {
        return this.second;
    }

    public SafetyEnum getSafety() {
        return this.safety;
    }

    @Override
    public String toString() {
        return "Hole(" + this.first + ", " + this.second + ", " + this.safety + ")";
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Hole)) {
            return false;
        }
        final Hole hole = (Hole)obj;
        return this.first == hole.first && this.second == hole.second && this.safety == hole.safety;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 31).append((Object)this.first).append((Object)this.first).append((Object)this.safety).toHashCode();
    }
}
