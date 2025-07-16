import { createRouter, createWebHistory } from 'vue-router'
import AdminLayout from '@/layouts/AdminLayout.vue'
import AdminCreateProduct from '@/views/admin/AdminCreateProduct.vue'
import AdminEditProduct from '@/views/admin/AdminEditProduct.vue'
import AdminProductList from '@/components/AdminProductList.vue'
import AdminCategoryList from '@/components/AdminCategoryList.vue'
import AdminCategoryEditView from '@/views/admin/AdminCategoryEditView.vue'

const routes = [
  {
    path: '/admin',
    component: AdminLayout,
    children: [
      {
        path: 'products',
        name: 'AdminProductList',
        component: AdminProductList
      },
      {
        path: 'products/create',
        name: 'AdminCreateProduct',
        component: AdminCreateProduct
      },
      {
        path: 'products/:id/edit',
        name: 'AdminEditProduct',
        component: AdminEditProduct,
        props: true
      },
      {
        path: 'categories',
        name: 'AdminCategoryList',
        component: AdminCategoryList
      },
      {
        path: 'categories/:id/edit',
        name: 'AdminCategoryEdit',
        component: AdminCategoryEditView,
        props: true
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
