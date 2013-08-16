package org.dynamicextensions.testrunner;

/**
 * @author Laurent Van der Linden
 */
public class BundleTest implements Comparable<BundleTest> {
  private String className;
  private Long bundleId;
  private String bundleName;

  public BundleTest(String className, long bundleId, String bundleName) {
    this.className = className;
    this.bundleId = bundleId;
    this.bundleName = bundleName;
  }

  public String getClassName() {
    return className;
  }

  public Long getBundleId() {
    return bundleId;
  }

  public String getBundleName() {
    return bundleName;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    BundleTest that = (BundleTest) o;

    if (!bundleId.equals(that.bundleId)) return false;
    if (!className.equals(that.className)) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = className.hashCode();
    result = 31 * result + bundleId.hashCode();
    return result;
  }

  @Override
  public String toString() {
    return "BundleTest{" +
        "className='" + className + '\'' +
        ", bundleId=" + bundleId +
        ", bundleName='" + bundleName + '\'' +
        '}';
  }

  @Override
  public int compareTo(BundleTest o) {
    return (getBundleId() + getClassName()).compareTo(o.getBundleId() + o.getClassName());
  }
}
