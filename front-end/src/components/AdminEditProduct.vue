<template>
  <div class="max-w-4xl mx-auto py-10 px-4">
    <h1 class="text-3xl font-bold text-center text-gray-800 mb-6">Редактирай продукт</h1>

    <form @submit.prevent="submitProduct" class="bg-white shadow-md rounded px-8 pt-6 pb-8 mb-4 space-y-6">
      <div>
        <label class="block text-gray-700 text-sm font-bold mb-2" for="title">Заглавие</label>
        <input v-model="product.title" id="title" type="text" placeholder="Заглавие"
          class="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:ring-2 focus:ring-pink" />
      </div>

      <div>
        <label class="block text-gray-700 text-sm font-bold mb-2" for="description">Описание</label>
        <quill-editor
          v-model:value="product.description"
          contentType="html"
          theme="snow"
          class="bg-white rounded shadow border"
          style="min-height: 200px"
        />
      </div>

      <div class="grid grid-cols-2 gap-4">
        <div>
          <label class="block text-gray-700 text-sm font-bold mb-2" for="price">Цена (лв)</label>
          <input v-model.number="product.price" id="price" type="number" min="0" step="0.01"
            class="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:ring-2 focus:ring-pink" />
        </div>

        <div>
          <label class="block text-gray-700 text-sm font-bold mb-2" for="salePrice">Промо цена (лв)</label>
          <input v-model.number="product.salePrice" id="salePrice" type="number" min="0" step="0.01"
            class="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:ring-2 focus:ring-pink" />
        </div>
      </div>

      <div>
        <label class="block text-gray-700 text-sm font-bold mb-2" for="slug">Slug (линк)</label>
        <input v-model="product.slug" id="slug" type="text" placeholder="my-product"
          class="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:ring-2 focus:ring-pink" />
      </div>

      <div>
        <label class="block text-gray-700 text-sm font-bold mb-2">Избери категория</label>
        <CategoryPicker
          :categories="allCategories"
          :selected-categories="product.categoryIds"
          @select="onCategorySelected"
          @unselect="onCategoryRemoved"
        />
      </div>

      <div class="mb-2">
        <label class="block text-gray-700 text-sm font-bold mb-2">Снимки</label>
        <input ref="imageInput" accept="image/*" type="file" multiple @change="handleImageUpload" class="hidden" />

        <button @click.prevent="$refs.imageInput.click()" type="button"
          class="inline-flex items-center gap-2 px-4 py-2 bg-[#F777AC] text-white text-sm font-semibold rounded hover:bg-pink-600 focus:outline-none focus:ring">
          📷 Качи снимки
        </button>

        <div class="flex gap-2 mt-2 flex-wrap justify-center">
          <div v-for="(img, index) in product.images" :key="index" class="relative">
            <img :src="img.preview || img" class="w-[100px] h-[100px] object-cover rounded border" />
            <button @click.prevent="removeImage(index)"
              class="absolute top-0 right-0 bg-red-500 text-white rounded-full p-1 text-xs">✕</button>
          </div>
        </div>
      </div>

      <div class="space-y-4">
        <h2 class="text-lg font-semibold text-gray-800">Варианти</h2>
        <div v-for="(variant, index) in product.variants" :key="index" class="space-y-4 border p-4 rounded shadow-md">
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">Име на вариант</label>
            <input v-model="variant.title" placeholder="Име на вариант"
              class="shadow border rounded py-2 px-3 text-gray-700 w-full focus:outline-none focus:ring-2 focus:ring-pink" />
          </div>

          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">Цена (лв)</label>
            <input v-model.number="variant.price" type="number" min="0" step="0.01"
              class="shadow border rounded py-2 px-3 text-gray-700 w-full focus:outline-none focus:ring-2 focus:ring-pink" />
          </div>

          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">Промо цена (лв)</label>
            <input v-model.number="variant.salePrice" type="number" min="0" step="0.01"
              class="shadow border rounded py-2 px-3 text-gray-700 w-full focus:outline-none focus:ring-2 focus:ring-pink" />
          </div>

          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">Снимка на варианта</label>
            <input type="file" accept="image/*" :ref="'variantImageInput_' + index" @change="handleVariantImageUpload($event, index)" class="hidden" />
            <button @click.prevent="$refs['variantImageInput_' + index][0].click()" type="button"
              class="inline-flex items-center gap-2 px-4 py-2 bg-[#F777AC] text-white text-sm font-semibold rounded hover:bg-pink-600 focus:outline-none focus:ring">
              📷 Качи снимка
            </button>
            <div v-if="variant.imagePreview" class="mt-2 relative w-[100px] h-[100px] mx-auto">
              <img :src="variant.imagePreview" class="w-full h-full object-cover rounded border" />
              <button @click.prevent="removeVariantImage(index)" class="absolute top-0 right-0 bg-red-500 text-white rounded-full p-1 text-xs">✕</button>
            </div>
          </div>

          <button @click.prevent="removeVariant(index)" class="text-red-500 hover:underline text-sm mt-2">Премахни варианта</button>
        </div>

        <button @click.prevent="addVariant" class="text-[#F777AC] hover:underline text-sm">+ Добави вариант</button>
      </div>

      <div class="pt-6">
        <button type="submit" class="bg-[#F777AC] hover:bg-pink-600 text-white font-bold py-2 px-4 rounded w-full">
          Запази промените
        </button>
      </div>
    </form>
  </div>
</template>

<script setup>
// @vue/compiler-sfc <script setup> macros
/* eslint-disable no-undef */
import { ref, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import axios from 'axios';
import { quillEditor } from 'vue3-quill';
import 'quill/dist/quill.snow.css';
import CategoryPicker from '@/components/CategoryPicker.vue';

const product = ref({
  title: '',
  description: '',
  price: 0,
  salePrice: 0,
  slug: '',
  active: true,
  categoryIds: [],
  images: [],
  variants: [],
});

const allCategories = ref([]);
const route = useRoute();
const router = useRouter();

onMounted(async () => {
  try {
    const res = await axios.get(`/api/admin/products/${route.params.id}`);
    const p = res.data;
    product.value = {
      ...p,
      categoryIds: p.categoryIds,
      images: p.imageFileNames.map(name => `http://192.168.0.68:8080/uploads/products/${p.id}/${name}`),
      variants: p.variants.map(v => ({
        ...v,
        imageFile: null,
        imagePreview: v.imageFileName ? `http://192.168.0.68:8080/uploads/products/${p.id}/${v.imageFileName}` : null,
      })),
    };
    const catRes = await axios.get('/api/admin/categories');
    allCategories.value = catRes.data;
  } catch (e) {
    console.error('Грешка при зареждане:', e);
  }
});

const onCategorySelected = (categoryId) => {
  if (!product.value.categoryIds.includes(categoryId)) {
    product.value.categoryIds.push(categoryId);
  }
};

const onCategoryRemoved = (categoryId) => {
  product.value.categoryIds = product.value.categoryIds.filter(id => id !== categoryId);
};

const handleImageUpload = (event) => {
  const newFiles = Array.from(event.target.files);
  const newImageObjects = newFiles.map(file => ({ file, preview: URL.createObjectURL(file) }));
  product.value.images.push(...newImageObjects);
};

const removeImage = (index) => {
  product.value.images.splice(index, 1);
};

const addVariant = () => {
  product.value.variants.push({ title: '', price: 0, salePrice: 0, active: true, imageFile: null, imagePreview: null });
};

const removeVariant = (index) => {
  product.value.variants.splice(index, 1);
};

const handleVariantImageUpload = (event, index) => {
  const file = event.target.files[0];
  if (!file) return;
  const reader = new FileReader();
  reader.onload = () => {
    product.value.variants[index].imageFile = file;
    product.value.variants[index].imagePreview = reader.result;
  };
  reader.readAsDataURL(file);
};

const removeVariantImage = (index) => {
  product.value.variants[index].imageFile = null;
  product.value.variants[index].imagePreview = null;
};

const submitProduct = async () => {
  try {
    const formData = new FormData();
    formData.append('product', new Blob([JSON.stringify(product.value)], { type: 'application/json' }));

    for (const image of product.value.images) {
      if (image.file) {
        formData.append('images', image.file);
      }
    }

    product.value.variants.forEach((variant) => {
      const variantData = { ...variant };
      delete variantData.imageFile;
      delete variantData.imagePreview;
      formData.append('variants', new Blob([JSON.stringify(variantData)], { type: 'application/json' }));

      if (variant.imageFile) {
        formData.append('variantImages', variant.imageFile);
      }
    });

    await axios.put(`/api/admin/products/${product.value.id}`, formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      },
    });

    alert('Продуктът е обновен успешно!');
    router.push('/admin/products');
  } catch (e) {
    console.error('Грешка при обновяване:', e);
    alert('Възникна грешка при запис.');
  }
};
</script>
