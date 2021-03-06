package nz.gen.geek_central.GLUseful;
/*
    Quaternion representation of 3D rotation transformations.

    Copyright 2011 by Lawrence D'Oliveiro <ldo@geek-central.gen.nz>.

    Licensed under the Apache License, Version 2.0 (the "License"); you may not
    use this file except in compliance with the License. You may obtain a copy of
    the License at

    http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
    WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
    License for the specific language governing permissions and limitations under
    the License.
*/

import android.opengl.GLES11;

public class Rotation implements android.os.Parcelable
  {
    public final float c, x, y, z;

    public Rotation
      (
        float AngleDegrees,
        float X,
        float Y,
        float Z
      )
      /* constructs a Rotation that rotates by the specified angle
        about the axis direction (X, Y, Z). */
      {
        final float Cos = android.util.FloatMath.cos((float)Math.toRadians(AngleDegrees / 2));
        final float Sin = android.util.FloatMath.sin((float)Math.toRadians(AngleDegrees / 2));
        final float Mag = android.util.FloatMath.sqrt(X * X + Y * Y + Z * Z);
        c = Cos;
        x = Sin * X / Mag;
        y = Sin * Y / Mag;
        z = Sin * Z / Mag;
      } /*Rotation*/

    public static final Rotation Null = new Rotation(0, 0, 0, 1);
      /* represents no rotation at all */

    private Rotation
      (
        float c,
        float x,
        float y,
        float z,
        Object Dummy
      )
      /* internal-use constructor with directly-computed components. Note
        this does not compensate for accumulated rounding errors. */
      {
        this.c = c;
        this.x = x;
        this.y = y;
        this.z = z;
      } /*Rotation*/

    public static android.os.Parcelable.Creator<Rotation> CREATOR =
      /* restore state from a Parcel. */
        new android.os.Parcelable.Creator<Rotation>()
          {
            public Rotation createFromParcel
              (
                android.os.Parcel Post
              )
              {
                final android.os.Bundle MyState = Post.readBundle();
                return
                    new Rotation
                      (
                        MyState.getFloat("c", Null.c),
                        MyState.getFloat("x", Null.x),
                        MyState.getFloat("y", Null.y),
                        MyState.getFloat("z", Null.z),
                        null
                      );
              } /*createFromParcel*/

            public Rotation[] newArray
              (
                int NrElts
              )
              {
                return
                    new Rotation[NrElts];
              } /*newArray*/
          } /*Parcelable.Creator*/;

    @Override
    public int describeContents()
      {
        return
            0; /* nothing special */
      } /*describeContents*/

    @Override
    public void writeToParcel
      (
        android.os.Parcel Post,
        int Flags
      )
      /* save state to a Parcel. */
      {
        final android.os.Bundle MyState = new android.os.Bundle();
        MyState.putFloat("c", c);
        MyState.putFloat("x", x);
        MyState.putFloat("y", y);
        MyState.putFloat("z", z);
        Post.writeBundle(MyState);
      } /*writeToParcel*/

    public Rotation inv()
      /* returns rotation by the opposite angle around the same axis. Or alternatively,
        the same angle around the opposite-pointing axis . */
      {
        return
            new Rotation(c, -x, -y, -z, null);
      } /*inv*/

    public Rotation mul
      (
        Rotation that
      )
      /* returns composition with another rotation. */
      {
        return
            new Rotation
              (
                this.c * that.c - this.x * that.x - this.y * that.y - this.z * that.z,
                this.y * that.z - this.z * that.y + this.c * that.x + that.c * this.x,
                this.z * that.x - this.x * that.z + this.c * that.y + that.c * this.y,
                this.x * that.y - this.y * that.x + this.c * that.z + that.c * this.z,
                null
              );
      } /*mul*/

    public Rotation mul
      (
        float Frac
      )
      /* returns the specified fraction of the rotation. */
      {
        final float Mag = android.util.FloatMath.sqrt(x * x + y * y + z * z);
        return
            Mag != 0.0f ?
                new Rotation((float)Math.toDegrees(2 * Math.atan2(Mag, c) * Frac), x / Mag, y / Mag, z / Mag)
            :
                new Rotation(0, 0, 0, 1);
      } /*mul*/

    public void Apply()
      /* applies the rotation to the currently-selected GL matrix. */
      {
        final float Mag = android.util.FloatMath.sqrt(x * x + y * y + z * z);
        if (Mag != 0.0f)
          {
            GLES11.glRotatef((float)Math.toDegrees(2 * Math.atan2(Mag, c)), x / Mag, y / Mag, z / Mag);
          } /*if*/
      } /*Apply*/

    public String toString()
      {
        return
            String.format
              (
                "Rotation(%e, %e, %e, %e)",
                c, x, y, z
              );
      } /*toString*/

  } /*Rotation*/
