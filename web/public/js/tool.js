Tool = {
  /**
   * 空校验 null或""都返回true
   */
  isEmpty: (obj) => {
    if ((typeof obj === 'string')) {
      return !obj || obj.replace(/\s+/g, "") === ""
    } else {
      return (!obj || JSON.stringify(obj) === "{}" || obj.length === 0);
    }
  },

  /**
   * 非空校验
   */
  isNotEmpty: (obj) => {
    return !Tool.isEmpty(obj);
  },

  /**
   * 对象复制
   * @param obj
   */
  copy: (obj) => {
    if (Tool.isNotEmpty(obj)) {
      return JSON.parse(JSON.stringify(obj));
    }
  },

  /**
   * 随机生成[len]长度的[radix]进制数
   * @param len
   * @param radix 默认62
   * @returns {string}
   */
  uuid: (len, radix = 62) => {
    const chars = '0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz'.split('');
    const uuid = [];
    radix = radix || chars.length;

    for (let i = 0; i < len; i++) {
      uuid[i] = chars[0 | Math.random() * radix];
    }

    return uuid.join('');
  }
};
