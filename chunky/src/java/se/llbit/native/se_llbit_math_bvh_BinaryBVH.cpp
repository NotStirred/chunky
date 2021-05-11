#include <se_llbit_math_bvh_BinaryBVH.h>

#include <vector>

#include <util/util.h>

const double OFFSET = 0.000001;

double quickAabbIntersect(double, double, double, float, float, float, float, float, float, double, double, double);

jmethodID intersectPrimitives;
JNIEXPORT void JNICALL Java_se_llbit_math_bvh_BinaryBVH_init
        (JNIEnv* env, jclass clazz) {
    intersectPrimitives = env->GetMethodID(clazz, "intersectPrimitives", "(Lse/llbit/math/Ray;I)Z");
    jclass rayClass = env->FindClass("se/llbit/math/Ray");
    jfieldID offset_fieldID = env->GetStaticFieldID(rayClass, "OFFSET", "D");
    if(OFFSET != env->GetStaticDoubleField(rayClass, offset_fieldID)) {
        chunky::jni::throwException(env, "Ray#OFFSET != native OFFSET");
    }
}
JNIEXPORT jboolean JNICALL Java_se_llbit_math_bvh_BinaryBVH_closestIntersection
        (JNIEnv* env, jobject thisObject, jobject ray, jdouble ray_t, jdouble ray_d_x, jdouble ray_d_y, jdouble ray_d_z, jdouble ray_o_x, jdouble ray_o_y, jdouble ray_o_z, jint depth, jlong pPacked) {
    auto* packed = (int32_t*)pPacked;

    bool hit = false;
    uint32_t currentNode = 0;
    std::vector<uint32_t> nodesToVisit(depth/2);

    const double rx = 1 / ray_d_x;
    const double ry = 1 / ray_d_y;
    const double rz = 1 / ray_d_z;

    while (true) {
        if (packed[currentNode] <= 0) {
            // Is leaf
            int primIndex = -packed[currentNode];
            hit = env->CallBooleanMethod(thisObject, intersectPrimitives, ray, primIndex) | hit;

            if (nodesToVisit.empty()) break;
            currentNode = nodesToVisit[nodesToVisit.size()-1];
            nodesToVisit.pop_back();
        } else {
            // Is branch, find closest node
            uint32_t offset = currentNode+7;
            double t1 = quickAabbIntersect(ray_o_x, ray_o_y, ray_o_z, chunky::util::signed_to_float(packed[offset+1]), chunky::util::signed_to_float(packed[offset+2]),
                                           chunky::util::signed_to_float(packed[offset+3]), chunky::util::signed_to_float(packed[offset+4]),
                                           chunky::util::signed_to_float(packed[offset+5]), chunky::util::signed_to_float(packed[offset+6]),
                                           rx, ry, rz);
            offset = packed[currentNode];
            double t2 = quickAabbIntersect(ray_o_x, ray_o_y, ray_o_z, chunky::util::signed_to_float(packed[offset+1]), chunky::util::signed_to_float(packed[offset+2]),
                                           chunky::util::signed_to_float(packed[offset+3]), chunky::util::signed_to_float(packed[offset+4]),
                                           chunky::util::signed_to_float(packed[offset+5]), chunky::util::signed_to_float(packed[offset+6]),
                                           rx, ry, rz);

            if (t1 > ray_t | t1 == -1) {
                if (t2 > ray_t | t2 == -1) {
                    if (nodesToVisit.empty()) break;
                    currentNode = nodesToVisit[nodesToVisit.size()-1];
                    nodesToVisit.pop_back();
                } else {
                    currentNode = packed[currentNode];
                }
            } else if (t2 > ray_t | t2 == -1) {
                currentNode += 7;
            } else if (t1 < t2) {
                nodesToVisit.push_back(packed[currentNode]);
                currentNode += 7;
            } else {
                nodesToVisit.push_back(currentNode + 7);
                currentNode = packed[currentNode];
            }
        }
    }

    return hit;
}

double quickAabbIntersect(double ray_o_x, double ray_o_y, double ray_o_z, float xmin, float xmax, float ymin, float ymax, float zmin, float zmax, double rx, double ry, double rz) {
    if (ray_o_x >= xmin && ray_o_x <= xmax && ray_o_y >= ymin && ray_o_y <= ymax && ray_o_z >= zmin && ray_o_z <= zmax) {
        return 0;
    }

    double tx1 = (xmin - ray_o_x) * rx;
    double tx2 = (xmax - ray_o_x) * rx;

    double ty1 = (ymin - ray_o_y) * ry;
    double ty2 = (ymax - ray_o_y) * ry;

    double tz1 = (zmin - ray_o_z) * rz;
    double tz2 = (zmax - ray_o_z) * rz;

    double tmin = chunky::util::max(chunky::util::max(chunky::util::min(tx1, tx2), chunky::util::min(ty1, ty2)), chunky::util::min(tz1, tz2));
    double tmax = chunky::util::min(chunky::util::min(chunky::util::max(tx1, tx2), chunky::util::max(ty1, ty2)), chunky::util::max(tz1, tz2));

    return (tmin <= tmax + OFFSET & tmin >= 0) ? tmin : -1;
}
