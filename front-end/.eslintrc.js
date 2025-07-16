module.exports = {
  root: true,
  env: {
    node: true,
  },
  extends: [
    'plugin:vue/vue3-essential',
    'eslint:recommended',
  ],
  parserOptions: {
    ecmaVersion: 2020,
  },
  rules: {
    'space-before-function-paren': 'off',
    'comma-dangle': 'off',
    'semi': 'off',
  },
};
