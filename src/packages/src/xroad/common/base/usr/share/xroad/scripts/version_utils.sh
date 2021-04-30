## $1: version number e.g. 7.0.0-0.20210430092553gitab82921.ubuntu18.04
## $2: number of the part to increment: 0 – major, 1 – minor, 2 – patch
## $3: how many versions to increment e.g. 1 for the next version
increment_version() {
  local delimiter=.
  local array=($(echo "${1%-*}" | tr $delimiter '\n'))
  array[$2]=$((array[$2]+$3))
  echo $(local IFS=$delimiter ; echo "${array[*]}")
}
