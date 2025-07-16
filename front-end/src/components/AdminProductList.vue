<template>
  <div class="max-w-7xl mx-auto py-10 px-4">
    <div class="flex justify-between items-center mb-6">
      <h1 class="text-3xl font-bold text-gray-800">Продукти</h1>
      <router-link
        to="/admin/products/create"
        class="bg-[#F777AC] text-white px-5 py-2 rounded-full hover:bg-pink-600 transition"
      >
        + Нов продукт
      </router-link>
    </div>

    <div class="flex flex-col sm:flex-row sm:items-center sm:justify-between mb-6 gap-4">
      <input
        v-model="search"
        type="text"
        placeholder="🔍 Търси по заглавие..."
        class="w-full sm:w-1/2 px-4 py-2 border rounded focus:outline-none focus:ring-2 focus:ring-pink-300"
      />

      <label class="inline-flex items-center gap-2 text-sm text-gray-700">
        <input type="checkbox" v-model="showOnlyActive" class="form-checkbox text-pink-600" />
        Покажи само активни
      </label>
    </div>

    <div class="overflow-x-auto bg-white rounded-lg shadow">
      <table class="min-w-full divide-y divide-gray-200">
        <thead class="bg-gray-50">
          <tr class="text-left text-gray-700 text-sm">
            <th class="px-6 py-3">Продукт</th>
            <th class="px-6 py-3">Цена</th>
            <th class="px-6 py-3">Промо цена</th>
            <th class="px-6 py-3">Активен</th>
            <th class="px-6 py-3 text-center">Действия</th>
          </tr>
        </thead>
        <tbody class="divide-y divide-gray-100">
          <tr
            v-for="product in filteredProducts"
            :key="product.id"
            class="hover:bg-gray-50 cursor-pointer transition-all group"
            :class="{ 'bg-gray-100 text-gray-400': !product.active }"
            @click="goToEdit(product.id)"
          >
            <td class="px-6 py-4 flex items-center gap-4">
              <img
                v-if="product.imageFileName"
                :src="getFullImageUrl(product.id, product.imageFileName)"
                class="w-14 h-14 object-cover rounded border"
                alt="product"
              />
              <div
                v-else
                class="w-14 h-14 flex items-center justify-center bg-gray-100 text-gray-400 rounded border text-xs"
              >
                Няма
              </div>
              <span class="font-medium">{{ product.title }}</span>
            </td>
            <td class="px-6 py-4">{{ Number(product.price).toFixed(2) }} лв</td>
            <td class="px-6 py-4 text-pink-600 font-medium">
              {{ product.salePrice != null ? Number(product.salePrice).toFixed(2) : '-' }} лв
            </td>
            <td class="px-6 py-4">
              <label class="inline-flex relative items-center cursor-pointer" @click.stop>
                <input
                  type="checkbox"
                  class="sr-only peer"
                  v-model="product.active"
                  @change.stop="toggleActive(product)"
                />
                <div class="w-11 h-6 bg-gray-200 rounded-full peer peer-checked:bg-[#F777AC] peer-focus:ring-2 peer-focus:ring-pink-300 transition-all"></div>
                <div class="absolute left-1 top-1 bg-white w-4 h-4 rounded-full transition-all peer-checked:translate-x-5"></div>
              </label>
            </td>
            <td class="px-6 py-4 text-center" @click.stop>
              <button
                @click="deleteProduct(product.id)"
                class="text-red-500 hover:text-red-700 text-sm font-semibold flex items-center justify-center gap-1"
              >
                🗑 <span>Изтрий</span>
              </button>
            </td>
          </tr>
          <tr v-if="!filteredProducts.length">
            <td colspan="5" class="text-center py-6 text-gray-400">Няма намерени продукти.</td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  name: 'AdminProductList',
  data() {
    return {
      products: [],
      search: '',
      showOnlyActive: false,
    };
  },
  computed: {
    filteredProducts() {
      return this.products.filter((p) => {
        const matchesTitle = p.title.toLowerCase().includes(this.search.toLowerCase());
        const matchesActive = this.showOnlyActive ? p.active : true;
        return matchesTitle && matchesActive;
      });
    },
  },
  async created() {
    try {
      const res = await axios.get('/api/admin/products');
      this.products = res.data;
    } catch (e) {
      console.error('Грешка при зареждане на продуктите:', e);
    }
  },
  methods: {
    async toggleActive(product) {
      try {
        await axios.patch(`/api/admin/products/${product.id}/active`, { active: product.active });
      } catch (e) {
        console.error('Грешка при промяна на активност:', e);
      }
    },
    async deleteProduct(id) {
      if (confirm('Сигурни ли сте, че искате да изтриете този продукт?')) {
        try {
          await axios.delete(`/api/admin/products/${id}`);
          this.products = this.products.filter(p => p.id !== id);
        } catch (e) {
          console.error('Грешка при изтриване:', e);
        }
      }
    },
    getFullImageUrl(productId, fileName) {
      return `http://localhost:8080/uploads/products/${productId}/${fileName}`;
    },
    goToEdit(id) {
      this.$router.push(`/admin/products/${id}/edit`);
    },
  },
};
</script>
